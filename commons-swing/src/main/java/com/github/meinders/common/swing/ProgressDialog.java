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
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import com.github.meinders.common.*;

/**
 * A standard progress-monitoring dialog, featuring a status message, progress
 * bar and an optional feedback list containing log entries. The feedback list
 * supports different colors for regular, warning and error messages.
 *
 * @version 0.2 (2005.08.03)
 * @author Gerrit Meinders
 */
public class ProgressDialog<T> extends JDialog implements WorkerListener<T>
{
    private Worker<T> worker = null;
    private Color errorColor = new Color(255, 0, 0);
    private Color warningColor = new Color(255, 168, 0);
    private int minimumProgressInterval = 10;
    private long lastProgressTime;

    private JLabel messageLabel;
    private JProgressBar progressBar;
    private DefaultListModel feedbackListModel;
    private JComponent feedbackComponent;
    private Action okAction;
    private Action cancelAction;

    private ResourceUtilities resources;
    private int warningCount;
    private int errorCount;

    /**
     * Constructs a new progress dialog with the given owner and title. The
     * default locale is used.
     *
     * @param owner the dialog's owner
     * @param title the dialog's title
     */
    public ProgressDialog(Dialog owner, String title) {
        this(owner, title, Locale.getDefault());
    }

    /**
     * Constructs a new progress dialog with the given owner and title. The
     * default locale is used.
     *
     * @param owner the dialog's owner
     * @param title the dialog's title
     */
    public ProgressDialog(Frame owner, String title) {
        this(owner, title, Locale.getDefault());
    }

    /**
     * Constructs a new progress dialog with the given owner and title, using
     * the given locale to retrieve localized resources.
     *
     * @param owner the dialog's owner
     * @param title the dialog's title
     * @param locale the locale to be used by the dialog
     */
    public ProgressDialog(Dialog owner, String title, Locale locale) {
        super(owner, title, false);
        ResourceBundle bundle = ResourceBundle.getBundle(
                "com.github.meinders.common.swing.ProgressDialog", locale);
        resources = new ResourceUtilities(bundle);
        init();
    }

    /**
     * Constructs a new progress dialog with the given owner and title, using
     * the given locale to retrieve localized resources.
     *
     * @param owner the dialog's owner
     * @param title the dialog's title
     * @param locale the locale to be used by the dialog
     */
    public ProgressDialog(Frame owner, String title, Locale locale) {
        super(owner, title, false);
        ResourceBundle bundle = ResourceBundle.getBundle(
                "com.github.meinders.common.swing.ProgressDialog", locale);
        resources = new ResourceUtilities(bundle);
        init();
    }

    /**
     * Returns the worker that's monitored by this progress dialog.
     *
     * @return the worker, or <code>null</code> if no worker has been set
     */
    public Worker<T> getWorker() {
        return worker;
    }

    /**
     * Sets the worker that's monitored by this progress dialog.
     *
     * @param worker the worker
     */
    public void setWorker(Worker<T> worker) {
        if (this.worker != null) {
            this.worker.removeWorkerListener(this);
        }
        this.worker = worker;
        if (worker != null) {
            worker.addWorkerListener(this);
        }
    }

    /**
     * Sets the color for error messages in the feedback list. If set to
     * <code>null</code>, error messages are displayed in the same color as
     * regular messages. Setting the error color has no influence on the color
     * of any messages that were added prior to calling this method.
     *
     * @param errorColor the color for error messages
     */
    public void setErrorColor(Color errorColor) {
        this.errorColor = errorColor;
    }

    /**
     * Returns the color for error messages in the feedback list.
     *
     * @return the color for error messages
     */
    public Color getErrorColor() {
        return errorColor;
    }

    /**
     * Sets the color for warning messages in the feedback list. If set to
     * <code>null</code>, warning messages are displayed in the same color as
     * regular messages. Setting the warning color has no influence on the color
     * of any messages that were added prior to calling this method.
     *
     * @param warningColor the color for warning messages
     */
    public void setWarningColor(Color warningColor) {
        this.warningColor = warningColor;
    }

    /**
     * Returns the color for warning messages in the feedback list.
     *
     * @return the color for warning messages
     */
    public Color getWarningColor() {
        return warningColor;
    }

    /**
     * Sets whether the feedback list should be visible or not.
     */
    public void setFeedbackVisible(boolean feedbackVisible) {
        feedbackComponent.setVisible(feedbackVisible);
    }

    /**
     * Returns whether the feedback list is visible or not.
     */
    public boolean isFeedbackVisible() {
        return feedbackComponent.isVisible();
    }

    /**
     * Returns the minimum interval between updates of the dialog as a result of
     * a progress event.
     *
     * @return the interval in milliseconds
     */
    public int getMinimumProgressInterval() {
        return minimumProgressInterval;
    }

    /**
     * Sets the minimum interval between updates of the dialog as a result of
     * a progress event. All other types of WorkerEvents are unaffected.
     *
     * @param minimumProgressInterval the interval in milliseconds
     */
    public void setMinimumProgressInterval(int minimumProgressInterval) {
        this.minimumProgressInterval = minimumProgressInterval;
    }

    public void stateChanged(WorkerEvent<T> e) {
        switch (e.getType()) {
        case STARTED:
            warningCount = 0;
            errorCount = 0;
            lastProgressTime = System.nanoTime();

            okAction.setEnabled(false);
            if (worker != null) {
                cancelAction.setEnabled(true);
            }
            addMessage(e.getMessage());
            break;

        case PROGRESS:
            long time = System.nanoTime();
            if (time - lastProgressTime < 1000000 * getMinimumProgressInterval()) {
                /*
                Ignore multiple progress events within the specified minimum
                progress interval.
                */

            } else {
                if (e.getLength() == WorkerEvent.UNKNOWN_LENGTH) {
                    progressBar.setIndeterminate(true);
                    progressBar.setString("");
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setString(null);
                    progressBar.setValue((int) e.getProgress(progressBar.getMaximum()));
                }
                if (e.getMessage() != null) {
                    messageLabel.setText(e.getMessage());
                }

                // set time of last progress update
                lastProgressTime = time;
            }
            break;

        case MESSAGE:
            addMessage(e.getMessage());
            break;

        case INTERRUPTED:
        case FINISHED:
            if (errorCount == 0 && warningCount == 0) {
                dispose();
                return;
            }

            progressBar.setValue(progressBar.getMaximum());

            String message;
            if (e.getMessage() == null) {
                message = resources.getString("errorWarningSummary",
                    errorCount, warningCount);
            } else {
                message = e.getMessage();
            }
            messageLabel.setText(message);
            addMessage(message);

            okAction.setEnabled(true);
            cancelAction.setEnabled(false);
            break;

        case ERROR:
            if (e.getMessage() == null) {
                addError(e.getCause().getLocalizedMessage());
            } else {
                addError(e.getMessage());
            }
            errorCount++;
            break;

        case WARNING:
            if (e.getMessage() == null) {
                addWarning(e.getCause().getLocalizedMessage());
            } else {
                addWarning(e.getMessage());
            }
            warningCount++;
            break;
        }
    }

    private void init() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (okAction.isEnabled()) {
                    okAction.actionPerformed(null);
                } else if (cancelAction.isEnabled()) {
                    cancelAction.actionPerformed(null);
                }
            }});
        createActions();
        setContentPane(createContentPane());
        setResizable(false);
        pack();

        Rectangle parentBounds = getParent().isVisible() ?
                getParent().getBounds() :
                new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        setLocation(parentBounds.x + (parentBounds.width - getWidth()) / 2,
                parentBounds.y + (parentBounds.height - getHeight()) / 2);
    }

    private void createActions() {
        okAction = new AbstractAction(resources.getString("ok")) {
            {
                setEnabled(false);
            }
            public void actionPerformed(ActionEvent e) {
                dispose();
            }};

        cancelAction = new AbstractAction(resources.getString("cancel")) {
            {
                setEnabled(false);
            }
            public void actionPerformed(ActionEvent e) {
                assert worker != null :
                        "cancelAction is enabled, but worker is null";
                worker.interrupt();
                dispose();
            }};
    }

    private JPanel createContentPane() {
        messageLabel = new JLabel("\n");
        messageLabel.setAlignmentX(CENTER_ALIGNMENT);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(1000);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(messageLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(progressBar);
        panel.add(createSouthPanel());

        Dimension preferredSize = panel.getPreferredSize();
        preferredSize.width = preferredSize.height * 3;
        panel.setPreferredSize(preferredSize);

        return panel;
    }

    private JPanel createSouthPanel() {
        feedbackComponent = createFeedbackComponent();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(createButtonPanel());
        panel.add(feedbackComponent);
        return panel;
    }

    /**
     * Creates the component that's used to display feedback (message, warnings
     * and errors) from the dialog's worker.
     */
    protected JComponent createFeedbackComponent() {
        feedbackListModel = new DefaultListModel();

        final JList feedbackList = new JList(feedbackListModel);
        feedbackList.setCellRenderer(new ColoredListCellRenderer());
        feedbackList.setAlignmentX(CENTER_ALIGNMENT);
        feedbackListModel.addListDataListener(new ListDataListener() {
            public void contentsChanged(ListDataEvent e) {}
            public void intervalRemoved(ListDataEvent e) {}
            public void intervalAdded(final ListDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        feedbackList.ensureIndexIsVisible(e.getIndex1());
                    }});
            }});

        return new JScrollPane(feedbackList);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(new JButton(okAction));
        panel.add(new JButton(cancelAction));
        return panel;
    }

    /**
     * Adds a warning message to the dialog's feedback component.
     */
    protected void addWarning(String message) {
        addMessage(message, warningColor);
    }

    /**
     * Adds a error message to the dialog's feedback component.
     */
    protected void addError(String message) {
        addMessage(message, errorColor);
    }

    /**
     * Adds a regular message to the dialog's feedback component.
     */
    protected void addMessage(String message) {
        addMessage(message, null);
    }

    /**
     * Adds a message to the dialog's feedback component in the given color. If
     * <code>color</code> is <code>null</code>, the list's default color is
     * used.
     */
    protected void addMessage(String message, Color color) {
        if (message != null) {
            Object value;
            if (color == null) {
                value = message;
            } else {
                value = new ColoredElement(message, color);
            }
            feedbackListModel.addElement(value);
        }
    }

    private class ColoredElement
            implements ColoredListCellRenderer.ColoredElement {
        private Object value;
        private Color color;

        public ColoredElement(Object value, Color color) {
            this.value = value;
            this.color = color;
        }

        public Object getValue() {
            return value;
        }

        public Color getColor() {
            return color;
        }
    }
}
