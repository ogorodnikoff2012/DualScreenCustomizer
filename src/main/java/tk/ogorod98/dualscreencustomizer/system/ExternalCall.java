/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ExternalCall {

  public static CompletableFuture<List<String>> runCmd(
      String cmd, Consumer<OutputStream> inputCallback) throws IOException {
    Process process = Runtime.getRuntime().exec(cmd);
    if (inputCallback != null) {
      inputCallback.accept(process.getOutputStream());
    }
    return process
        .onExit()
        .thenApply(
            (p) ->
                new BufferedReader(new InputStreamReader(p.getInputStream()))
                    .lines()
                    .collect(Collectors.toList()));
  }

  public static CompletableFuture<List<String>> runCmd(String cmd) throws IOException {
    return runCmd(cmd, null);
  }

  Consumer<OutputStream> feedBytes(final byte[] bytes) {
    return (out) -> {
      try {
        out.write(bytes);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } finally {
        try {
          out.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
  }

  public static int getPid() throws IOException {
    File f = new File("/proc/self");
    return Integer.parseInt(f.toPath().toRealPath().getFileName().toString());
  }
}
