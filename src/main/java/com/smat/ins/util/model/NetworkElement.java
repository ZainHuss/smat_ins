package com.smat.ins.util.model;

import java.io.Serializable;

public class NetworkElement implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1511867924693726764L;
	private String name;
    private String image;

    public NetworkElement() {
    }

    public NetworkElement(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return name;
    }

}
