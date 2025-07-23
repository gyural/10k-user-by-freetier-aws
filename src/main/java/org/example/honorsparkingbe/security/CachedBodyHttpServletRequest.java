package org.example.honorsparkingbe.security;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

  private final byte[] cachedBody;

  /**
   * Constructs a new CachedBodyHttpServletRequest by reading and caching the entire body of the given HttpServletRequest.
   *
   * @param request the original HttpServletRequest whose body will be cached
   * @throws IOException if an I/O error occurs while reading the request body
   */
  public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
    super(request);
    InputStream requestInputStream = request.getInputStream();
    this.cachedBody = requestInputStream.readAllBytes();
  }

  /**
   * Returns a ServletInputStream that reads from the cached request body.
   *
   * This allows the request body to be read multiple times without consuming the original input stream.
   *
   * @return a ServletInputStream backed by the cached request body
   */
  @Override
  public ServletInputStream getInputStream() {
    return new CachedBodyServletInputStream(this.cachedBody);
  }

  /**
   * Returns a BufferedReader for reading the cached request body.
   *
   * @return a BufferedReader that reads from the cached request body
   */
  @Override
  public BufferedReader getReader() {
    return new BufferedReader(new InputStreamReader(this.getInputStream()));
  }

  private static class CachedBodyServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream buffer;

    /**
     * Constructs a new ServletInputStream that reads from the provided byte array.
     *
     * @param contents the byte array to be used as the input stream source
     */
    public CachedBodyServletInputStream(byte[] contents) {
      this.buffer = new ByteArrayInputStream(contents);
    }

    /**
     * Indicates whether all data from the input stream has been read.
     *
     * @return true if no more bytes are available to read; false otherwise
     */
    @Override
    public boolean isFinished() {
      return buffer.available() == 0;
    }

    /**
     * Indicates that the input stream is always ready to be read.
     *
     * @return true, as the stream does not require asynchronous readiness checks
     */
    @Override
    public boolean isReady() {
      return true;
    }

    /**
     * Throws {@code UnsupportedOperationException} as asynchronous reading is not supported by this input stream.
     *
     * @param listener the {@link ReadListener} to be set (not supported)
     * @throws UnsupportedOperationException always thrown to indicate unsupported operation
     */
    @Override
    public void setReadListener(ReadListener listener) {
      throw new UnsupportedOperationException();
    }

    /**
     * Reads the next byte of data from the cached input stream.
     *
     * @return the next byte of data, or -1 if the end of the stream is reached
     */
    @Override
    public int read() {
      return buffer.read();
    }
  }
}