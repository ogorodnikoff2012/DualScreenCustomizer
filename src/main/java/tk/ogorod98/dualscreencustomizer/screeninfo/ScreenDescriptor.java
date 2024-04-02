/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.screeninfo;

import java.util.Objects;

public class ScreenDescriptor {
  public String identifier;
  public String vendorName;
  public String modelName;

  public ScreenDescriptor(String identifier, String vendorName, String modelName) {
    this.identifier = identifier;
    this.vendorName = vendorName;
    this.modelName = modelName;
  }

  public ScreenDescriptor() {
    this("", "", "");
  }

  @Override
  public String toString() {
    return "ScreenDescriptor{"
        + "identifier='"
        + identifier
        + '\''
        + ", vendorName='"
        + vendorName
        + '\''
        + ", modelName='"
        + modelName
        + '\''
        + '}';
  }

  public String getDisplayName() {
    return "(" + identifier + ", " + vendorName + ", " + modelName + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ScreenDescriptor that = (ScreenDescriptor) o;
    return Objects.equals(identifier, that.identifier)
        && Objects.equals(vendorName, that.vendorName)
        && Objects.equals(modelName, that.modelName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier, vendorName, modelName);
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public static class Builder {
    private String identifier = "";
    private String vendorName = "";
    private String modelName = "";

    public Builder withIdentifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    public Builder withVendorName(String vendorName) {
      this.vendorName = vendorName;
      return this;
    }

    public Builder withModelName(String modelName) {
      this.modelName = modelName;
      return this;
    }

    public ScreenDescriptor build() {
      return new ScreenDescriptor(identifier, vendorName, modelName);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
