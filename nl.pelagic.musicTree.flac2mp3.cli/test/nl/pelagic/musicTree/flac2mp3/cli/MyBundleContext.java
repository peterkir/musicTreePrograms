package nl.pelagic.musicTree.flac2mp3.cli;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;

import org.junit.Ignore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

@Ignore
@SuppressWarnings("javadoc")
public class MyBundleContext implements BundleContext {

  public String property = null;

  @Override
  public String getProperty(String key) {
    return property;
  }

  @Override
  public Bundle getBundle() {
    return null;
  }

  @Override
  public Bundle installBundle(String location) throws BundleException {
    return null;
  }

  @Override
  public Bundle installBundle(String location, InputStream input) throws BundleException {
    return null;
  }

  @Override
  public Bundle getBundle(long id) {
    return null;
  }

  @Override
  public Bundle[] getBundles() {
    return null;
  }

  @Override
  public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
    /* empty */
  }

  @Override
  public void addServiceListener(ServiceListener listener) {
    /* empty */
  }

  @Override
  public void removeServiceListener(ServiceListener listener) {
    /* empty */
  }

  @Override
  public void addBundleListener(BundleListener listener) {
    /* empty */
  }

  @Override
  public void removeBundleListener(BundleListener listener) {
    /* empty */
  }

  @Override
  public void addFrameworkListener(FrameworkListener listener) {
    /* empty */
  }

  @Override
  public void removeFrameworkListener(FrameworkListener listener) {
    /* empty */
  }

  @Override
  public ServiceRegistration registerService(String[] clazzes, Object service, Dictionary properties) {
    return null;
  }

  @Override
  public ServiceRegistration registerService(String clazz, Object service, Dictionary properties) {
    return null;
  }

  @Override
  public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
    return null;
  }

  @Override
  public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
    return null;
  }

  @Override
  public ServiceReference getServiceReference(String clazz) {
    return null;
  }

  @Override
  public Object getService(ServiceReference reference) {
    return null;
  }

  @Override
  public boolean ungetService(ServiceReference reference) {
    return false;
  }

  @Override
  public File getDataFile(String filename) {
    return null;
  }

  @Override
  public Filter createFilter(String filter) throws InvalidSyntaxException {
    return null;
  }

}
