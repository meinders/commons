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

package com.github.meinders.common;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

@XmlRootElement
public class ApplicationDescriptor {
	public static void main(String[] args) throws Exception {
		JAXBContext context = JAXBContext.newInstance(ApplicationDescriptor.class);

		Unmarshaller unmarshaller = context.createUnmarshaller();
		ApplicationDescriptor descriptor = (ApplicationDescriptor) unmarshaller.unmarshal(new File(
		        "../opwViewer/template-application.xml"));
		descriptor.version.buildDate = new Date();

		Marshaller marshaller = context.createMarshaller();

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		marshaller.marshal(descriptor, document);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(
		        "{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(new DOMSource(document), new StreamResult(
		        System.out));
	}

	private String title;

	private String shortName;

	private String vendor;

	private String vendorURL;

	private String vendorEmail;

	private Version version;

	private String copyright;

	/**
	 * Constructs a new application descriptor.
	 */
	public ApplicationDescriptor() {
		// No initialization is needed.
	}

	@XmlElement
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlElement(name = "short-name")
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@XmlElement
	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	@XmlElement
	public String getVendorURL() {
		return vendorURL;
	}

	public void setVendorURL(String vendorURL) {
		this.vendorURL = vendorURL;
	}

	@XmlElement
	public String getVendorEmail() {
		return vendorEmail;
	}

	public void setVendorEmail(String vendorEmail) {
		this.vendorEmail = vendorEmail;
	}

	@XmlElement
	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	@XmlElement
	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	private static class Version {
		private int major = 1;

		private Integer minor;

		private Integer revision;

		private Integer buildNumber;

		private Date buildDate;

		@SuppressWarnings("unused")
		private Version() {
			// Required for JAXB.
		}

		public Version(int major) {
			super();
			this.major = major;
		}

		public Version(int major, int minor) {
			super();
			this.major = major;
			this.minor = minor;
		}

		public Version(int major, int minor, int revision) {
			super();
			this.major = major;
			this.minor = minor;
			this.revision = revision;
		}

		@XmlAttribute
		public int getMajor() {
			return major;
		}

		public void setMajor(int major) {
			this.major = major;
		}

		@XmlAttribute
		public Integer getMinor() {
			return minor;
		}

		public void setMinor(Integer minor) {
			this.minor = minor;
		}

		@XmlAttribute
		public Integer getRevision() {
			return revision;
		}

		public void setRevision(Integer revision) {
			this.revision = revision;
		}

		@XmlAttribute
		public Integer getBuildNumber() {
			return buildNumber;
		}

		public void setBuildNumber(Integer buildNumber) {
			this.buildNumber = buildNumber;
		}

		@XmlElement
		public Date getBuildDate() {
			return buildDate;
		}

		public void setBuildDate(Date buildDate) {
			this.buildDate = buildDate;
		}

		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();

			result.append(major);
			if (minor != null) {
				result.append('.');
				result.append(minor);
				if (revision != null) {
					result.append('.');
					result.append(revision);
				}
			} else if (revision != null) {
				result.append(" rev ");
				result.append(revision);
			}

			if (buildNumber != null) {
				result.append(" (build ");
				result.append(buildNumber);
				if (buildDate != null) {
					DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

					result.append(", ");
					result.append(dateFormat.format(buildDate));
				}
				result.append(')');
			} else if (buildDate != null) {
				DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

				result.append(" (");
				result.append(dateFormat.format(buildDate));
				result.append(')');
			}

			return result.toString();
		}
	}
}
