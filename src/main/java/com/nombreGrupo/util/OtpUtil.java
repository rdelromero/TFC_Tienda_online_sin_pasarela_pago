package com.nombreGrupo.util;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class OtpUtil {

  public String generarOtp() {
    int naturalAleatorio = new Random().nextInt(999999);
    String naturalSalida = Integer.toString(naturalAleatorio);

    while (naturalSalida.length() < 6) {
    	naturalSalida = "0" + naturalSalida;
    }
    return naturalSalida;
  }
}
