package quickfix.examples.banzai.utils;

import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.BuilderFactory;

/**
 * Class that will load an FXML file, using a Spring Application Context to create the controllers
 * (and optionally the instance elements in the FXML itself).
 *
 * It is required that an applicationContext is provided, and also required that either a location
 * or an inputStream (but not both) are provided. It is optional that a resources and/or charset is
 * provided.
 *
 * Simple usage:
 *
 * <pre>
 * Parent root = SpringFXMLLoader.create()
 *     .applicationContext(myAppContext)
 *     .location(getClass().getResource("myApp.fxml"))
 *     .load();
 * </pre>
 *
 * If access to the controller is needed, a reference must be kept to the SpringFXMLLoader:
 *
 * <pre>
 * SpringFXMLLoader<Parent, MyControllerClass> loader =
 *     SpringFXMLLoader.create()
 *     .applicationContext(myAppContext)
 *     .location(getClass().getResource("myApp.fxml"))
 *     .build();
 * Parent root = loader.load();
 * MyController controller = loader.getController();
 * </pre>
 *
 * @param <S> The type of the root element of the FXML.
 * @param <T> The type of the controller.
 */

public class SpringFXMLLoader<S, T> {
  private final URL location;
  private final InputStream inputStream;
  private final ApplicationContext applicationContext;
  private final ResourceBundle resources;
  private final Charset charset;
  private T controller;

  private boolean loaded;

  private SpringFXMLLoader(final ApplicationContext applicationContext, final URL location,
                           final InputStream inputStream, final ResourceBundle resources, final Charset charset) {
    this.applicationContext = applicationContext;
    this.location = location;
    this.inputStream = inputStream;
    this.resources = resources;
    this.charset = charset;
  }

  private S load() throws IOException {
    if (this.loaded) {
      throw new IllegalStateException("Cannot load fxml multiple times");
    }
    final FXMLLoader loader = new FXMLLoader();
    if (this.charset != null) {
      loader.setCharset(this.charset);
    }
    if (this.resources != null) {
      loader.setResources(this.resources);
    }
    if (this.location != null) {
      loader.setLocation(this.location);
    }
    loader.setControllerFactory(this.applicationContext::getBean);
    loader.setBuilderFactory(new BuilderFactory() {

      JavaFXBuilderFactory defaultFactory = new JavaFXBuilderFactory();

      @Override
      public javafx.util.Builder<?> getBuilder(final Class<?> type) {
        final String[] beanNames = SpringFXMLLoader.this.applicationContext.getBeanNamesForType(type);
        if (beanNames.length == 1) {
          return (javafx.util.Builder<Object>) () -> SpringFXMLLoader.this.applicationContext.getBean(beanNames[0]);
        } else {
          return this.defaultFactory.getBuilder(type);
        }
      }
    });
    final S root;
    if (this.location != null) {
      root = loader.load();
    } else if (this.inputStream != null) {
      root = loader.load(this.inputStream);
    } else {
      throw new AssertionError("SpringFXMLLoader constructed without location or input stream");
    }
    this.controller = loader.getController();
    this.loaded = true;
    return root;
  }

  public T getController() {
    if (!this.loaded) {
      throw new IllegalStateException("Controller is only available after loading");
    }
    return this.controller;
  }

  public static Builder create() {
    return new Builder();
  }

  public static class Builder {
    private ApplicationContext applicationContext;
    private URL location;
    private InputStream inputStream;
    private ResourceBundle resources;
    private Charset charset;

    public Builder applicationContext(final ApplicationContext applicationContext) {
      this.applicationContext = applicationContext;
      return this;
    }

    public Builder location(final URL location) {
      if (this.inputStream != null) {
        throw new IllegalStateException("Cannot specify location and input stream");
      }
      this.location = location;
      return this;
    }

    public Builder inputStream(final InputStream inputStream) {
      if (this.location != null) {
        throw new IllegalStateException("Cannot specify location and input stream");
      }
      this.inputStream = inputStream;
      return this;
    }

    public Builder resources(final ResourceBundle resources) {
      this.resources = resources;
      return this;
    }

    public Builder charset(final Charset charset) {
      this.charset = charset;
      return this;
    }

    public <S, T> SpringFXMLLoader<S, T> build() {
      if (this.applicationContext == null) {
        throw new IllegalStateException("Application context not specified");
      }
      if (this.location == null && this.inputStream == null) {
        throw new IllegalStateException("Must specify exactly one of location or inputStream");
      }
      if (this.charset == null) {
        this.charset = Charset.defaultCharset();
      }
      return new SpringFXMLLoader<>(this.applicationContext, this.location, this.inputStream, this.resources, this.charset);
    }

    public <S> S load() throws IOException {
      return this.<S, Object>build().load();
    }
  }

}
