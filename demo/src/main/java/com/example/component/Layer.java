package com.example.component;

/** Ebene einer Figur: größerer zIndex = weiter oben (wird später gezeichnet, wird zuerst getroffen). */
public class Layer implements Component {
    public int zIndex;

    public Layer(int zIndex) {
        this.zIndex = zIndex;
    }
}
