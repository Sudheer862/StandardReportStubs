package com.medassets.report.client.flex;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class FlexReport_BinderImpl_GenBundle_default_StaticClientBundleGenerator implements com.medassets.report.client.flex.FlexReport_BinderImpl_GenBundle {
  private static FlexReport_BinderImpl_GenBundle_default_StaticClientBundleGenerator _instance0 = new FlexReport_BinderImpl_GenBundle_default_StaticClientBundleGenerator();
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
    }
    return null;
  }-*/;
}
