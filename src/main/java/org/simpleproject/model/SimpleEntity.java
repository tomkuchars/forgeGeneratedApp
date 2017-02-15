package org.simpleproject.model;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
@Entity
public class SimpleEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column(name = "name")
	private String name;

	@Column(name = "simpleString")
	private String simpleString;

	@Column(name = "anotherString")
	private String anotherString;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SimpleEntity)) {
			return false;
		}
		SimpleEntity other = (SimpleEntity) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public String getSimpleString() {
		return simpleString;
	}

	public void setSimpleString(String simplField) {
		this.simpleString = simplField;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAnotherString() {
		return anotherString;
	}

	public void setAnotherString(String anotherString) {
		this.anotherString = anotherString;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (id != null)
			result += "id: " + id;
		result += ", version: " + version;
		if (simpleString != null && !simpleString.trim().isEmpty())
			result += ", simpleString: " + simpleString;
		if (name != null && !name.trim().isEmpty())
			result += ", name: " + name;
		if (anotherString != null && !anotherString.trim().isEmpty())
			result += ", anotherString: " + anotherString;
		return result;
	}
}