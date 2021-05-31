package serviceproxy.server.spring;

import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DaprController {
    
  /**
   * Callback API for health checks from Dapr's sidecar.
   */
  @GetMapping(path = "/healthz")
  public void healthz() {
  }
  
  /**
   * Returns Dapr's configuration for Actors.
   * @return Actor's configuration.
   * @throws IOException If cannot generate configuration.
   */
  @GetMapping(path = "/dapr/config", produces = MediaType.APPLICATION_JSON_VALUE)
  public byte[] daprConfig() throws IOException {
    return new byte[0];
  }
}
