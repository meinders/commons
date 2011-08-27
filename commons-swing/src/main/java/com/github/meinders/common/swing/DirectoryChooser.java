/*
 * Copyright 2018 Gerrit Meinders
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.meinders.common.swing;

import java.awt.*;
import java.awt.Dialog.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.*;
import javax.swing.tree.*;

import com.github.meinders.common.*;
import com.github.meinders.common.SwingWorker;

public class DirectoryChooser extends JTree implements TreeSelectionListener,
        MouseListener {
	private static final String SELECTED_DIRECTORY_PROPERTY = "selectedDirectory";

	private static final ResourceUtilities res = new ResourceUtilities(
	        DirectoryChooser.class.getName());

	private static FileSystemView fsv = FileSystemView.getFileSystemView();

	/*--- Begin Public API -----*/

	private SwingWorker nodeExpansionThread = null;

	private TreeSelectionModel treeSelectionModel = null;

	private javax.swing.Timer disableTreeSelcetionTimer = null;

	private boolean nodeExpansionInProcess = false;

	private boolean lookAndFeelIcons = true;

	private boolean dialogCancelled;

	private boolean doubleClickToConfirm = false;

	public DirectoryChooser() {
		this(null);
	}

	public DirectoryChooser(File dir) {
		super(new DirNode(fsv.getRoots()[0]));
		addTreeWillExpandListener(new MyTreeWillExpandListener());
		getSelectionModel().setSelectionMode(
		        TreeSelectionModel.SINGLE_TREE_SELECTION);
		setCellRenderer(new LookAndFeelDirRenderer());
		setSelectedDirectory(dir);
		addTreeSelectionListener(this);
		addMouseListener(this);
	}

	public void setLookAndFeelIcons(boolean lookAndFeelIcons) {
		if (this.lookAndFeelIcons != lookAndFeelIcons) {
			this.lookAndFeelIcons = lookAndFeelIcons;
			setCellRenderer(lookAndFeelIcons ? new LookAndFeelDirRenderer()
			        : new SystemDirRenderer());
		}
	}

	public boolean isLookAndFeelIcons() {
		return lookAndFeelIcons;
	}

	private void enableTreeSelection() {
		if (treeSelectionModel != null) {
			setSelectionModel(treeSelectionModel);
			treeSelectionModel = null;
		}
	}

	private void disableTreeSelection() {
		disableTreeSelcetionTimer = new javax.swing.Timer(100,
		        new ActionListener() {
			        public void actionPerformed(ActionEvent evt) {
				        if (nodeExpansionInProcess
				                && (treeSelectionModel == null)) {
					        treeSelectionModel = getSelectionModel();

					        setSelectionModel(null);
				        } else {
					        stopTimer();
				        }
			        }
		        });
		disableTreeSelcetionTimer.start();
	}

	private void stopTimer() {
		if (disableTreeSelcetionTimer != null) {
			disableTreeSelcetionTimer.stop();
		}
	}

	private class MyTreeWillExpandListener implements TreeWillExpandListener {
		public void treeWillExpand(TreeExpansionEvent evt)
		        throws ExpandVetoException {
			if (nodeExpansionInProcess) {
				throw new ExpandVetoException(evt);
			}
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			TreePath path = evt.getPath();
			DirNode node = (DirNode) path.getLastPathComponent();
			boolean areChildrenLoaded = node.areChildrenLoaded();
			if (!areChildrenLoaded) {
				nodeExpansionThread = new NodeExpansionThread(path);
				nodeExpansionThread.start();
				nodeExpansionInProcess = true;
				disableTreeSelection();
				throw new ExpandVetoException(evt);
			}
			enableTreeSelection();
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		public void treeWillCollapse(TreeExpansionEvent evt)
		        throws ExpandVetoException {
			if (nodeExpansionInProcess) {
				throw new ExpandVetoException(evt);
			}
		}
	}

	private class NodeExpansionThread extends SwingWorker {
		TreePath treePath = null;

		public NodeExpansionThread(TreePath path) {
			this.treePath = path;
		}

		@Override
		public Object construct() {
			DirNode node = (DirNode) treePath.getLastPathComponent();
			node.children();
			nodeExpansionInProcess = false;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					expandPath(treePath);
				}
			});
			return node;
		}
	}

	public void setSelectedDirectory(File selectedDirectory) {
		final File dir = (selectedDirectory == null) ? fsv.getDefaultDirectory()
		        : selectedDirectory;
		setSelectionPath(mkPath(dir));
	}

	public File getSelectedDirectory() {
		DirNode node = (DirNode) getLastSelectedPathComponent();
		if (node != null) {
			File dir = node.getDir();
			if (fsv.isFileSystem(dir)) {
				return dir;
			}
		}
		return null;
	}

	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}

	public ActionListener[] getActionListeners() {
		return listenerList.getListeners(ActionListener.class);
	}

	public void scrollSelectionToVisible() {
		scrollRowToVisible(Math.max(0, getMinSelectionRow() - 4));
	}

	/*--- End Public API -----*/

	/*--- TreeSelectionListener Interface -----*/

	public void valueChanged(TreeSelectionEvent ev) {
		File oldDir = null;
		TreePath oldPath = ev.getOldLeadSelectionPath();
		if (oldPath != null) {
			oldDir = ((DirNode) oldPath.getLastPathComponent()).getDir();
			if (!fsv.isFileSystem(oldDir)) {
				oldDir = null;
			}
		}
		File newDir = getSelectedDirectory();
		firePropertyChange(SELECTED_DIRECTORY_PROPERTY, oldDir, newDir);
	}

	/*--- MouseListener Interface -----*/

	public void mousePressed(MouseEvent e) {
		if (doubleClickToConfirm && (e.getClickCount() == 2)) {
			TreePath path = getPathForLocation(e.getX(), e.getY());
			if (path != null && path.equals(getSelectionPath())
			        && getSelectedDirectory() != null) {

				fireActionPerformed("dirSelected", e);
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		/* unused event from MouseListener interface */
	}

	public void mouseClicked(MouseEvent e) {
		/* unused event from MouseListener interface */
	}

	public void mouseEntered(MouseEvent e) {
		/* unused event from MouseListener interface */
	}

	public void mouseExited(MouseEvent e) {
		/* unused event from MouseListener interface */
	}

	/*--- Private Section ------*/

	private TreePath mkPath(File dir) {
		if (dir != null) {
			DirNode root = (DirNode) getModel().getRoot();
			if (root.getDir().equals(dir)) {
				return new TreePath(root);
			}

			File parentDirectory = fsv.getParentDirectory(dir);
			if (parentDirectory == null) {
				parentDirectory = dir.getParentFile();
			}

			TreePath parentPath = mkPath(parentDirectory);
			DirNode parentNode = (DirNode) parentPath.getLastPathComponent();
			Enumeration enumeration = parentNode.children();
			while (enumeration.hasMoreElements()) {
				DirNode child = (DirNode) enumeration.nextElement();
				if (child.getDir().equals(dir)) {
					return parentPath.pathByAddingChild(child);
				}
			}

			// as close as we can get
			return parentPath;
		}

		return null;
	}

	private void fireActionPerformed(String command, InputEvent evt) {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
		        command, evt.getWhen(), evt.getModifiers());
		ActionListener[] listeners = getActionListeners();
		for (int i = listeners.length - 1; i >= 0; i--) {
			listeners[i].actionPerformed(e);
		}
	}

	private static class DirNode extends DefaultMutableTreeNode {
		private boolean chlidrenLoaded = false;

		private int childrenCount = 0;

		DirNode(File dir) {
			super(dir);
		}

		public File getDir() {
			return (File) userObject;
		}

		public boolean areChildrenLoaded() {
			return chlidrenLoaded;
		}

		@Override
		public int getChildCount() {
			if (!chlidrenLoaded) {
				populateChildren();
				childrenCount = super.getChildCount();
				chlidrenLoaded = true;
			}
			return childrenCount;
		}

		@Override
		public Enumeration children() {
			if (!chlidrenLoaded) {
				populateChildren();
				childrenCount = super.getChildCount();
				chlidrenLoaded = true;
			}
			return super.children();
		}

		@Override
		public boolean isLeaf() {
			return false;
		}

		private void populateChildren() {
			if (children == null) {
				File[] files = fsv.getFiles(getDir(), true);
				Arrays.sort(files);
				for (int i = 0; i < files.length; i++) {
					File f = files[i];
					if (fsv.isTraversable(f).booleanValue()) {
						insert(new DirNode(f),

						(children == null) ? 0 : children.size());
					}
				}
			}
		}

		@Override
		public String toString() {
			return fsv.getSystemDisplayName(getDir());
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof DirNode && userObject.equals(((DirNode) o).userObject));
		}
	}

	private class SystemDirRenderer extends DefaultTreeCellRenderer {
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
		        boolean sel, boolean expanded, boolean leaf, int row,
		        boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
			        leaf, row, hasFocus);
			if (value instanceof DirNode) {
				DirNode node = (DirNode) value;
				setIcon(fsv.getSystemIcon(node.getDir()));
			}
			return this;
		}
	}

	private class LookAndFeelDirRenderer extends DefaultTreeCellRenderer {
		private final FileView fileView;

		{
			final JFileChooser fileChooser = new JFileChooser();
			final FileChooserUI ui = fileChooser.getUI();
			fileView = ui.getFileView(fileChooser);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
		        boolean sel, boolean expanded, boolean leaf, int row,
		        boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
			        leaf, row, hasFocus);
			if (value instanceof DirNode) {
				DirNode node = (DirNode) value;
				setIcon(fileView.getIcon(node.getDir()));
			}
			return this;
		}
	}

	public boolean showDialog() {
		return showDialog((Window) null);
	}

	public boolean showDialog(Window parent) {
		return showDialog(parent, res.getString("ok"));
	}

	public boolean showDialog(Window parent, String confirmText) {
		final JDialog dialog = new JDialog(parent, res.getString("title"),
		        ModalityType.APPLICATION_MODAL);

		final JButton okButton = new JButton(confirmText);
		final JButton cancelButton = new JButton(res.getString("cancel"));

		final JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		contentPane.add(new JScrollPane(this), BorderLayout.CENTER);
		dialog.setContentPane(contentPane);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object c = e.getSource();
				if (c == okButton || c == DirectoryChooser.this) {
					dialogCancelled = false;
				}
				dialog.setVisible(false);
			}
		};

		addActionListener(actionListener);
		okButton.addActionListener(actionListener);
		cancelButton.addActionListener(actionListener);

		addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev) {
				if (ev.getPropertyName().equals(SELECTED_DIRECTORY_PROPERTY)) {
					okButton.setEnabled(getSelectedDirectory() != null);
				}
			}
		});

		dialog.setSize(300, 350);

		Toolkit toolkit = dialog.getToolkit();
		Dimension screen = toolkit.getScreenSize();
		dialog.setLocation((screen.width - dialog.getWidth()) / 2,
		        (screen.height - dialog.getHeight()) / 2);

		scrollSelectionToVisible();
		dialogCancelled = true;
		dialog.setVisible(true);

		return !dialogCancelled;
	}
}
