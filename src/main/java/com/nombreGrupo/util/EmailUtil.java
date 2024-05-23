package com.nombreGrupo.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.nombreGrupo.modelo.entities.Pedido;
import com.nombreGrupo.services.PedidoService;
import com.nombreGrupo.modelo.entities.LineaFacturacion;

@Component
public class EmailUtil {

  @Autowired
  private JavaMailSender javaMailSender;
  @Autowired
  private PedidoService pedidoService;
  
  public void enviarEmailConUUIDParaVerificarEmail(String nombreDestinatario, String direccionEmail, String UUIDString) {
      SimpleMailMessage mensaje = new SimpleMailMessage();
      mensaje.setTo(direccionEmail);
      mensaje.setSubject("[nombrePagina.com] Verificación de Cuenta");

      // Personalizar el mensaje para saludar al usuario por su nombre
      String text = String.format(
              "Hola %s,\n\nPara verificar tu cuenta, haz clic en el siguiente enlace: %s\n\nSaludos,\nEl Equipo",
              nombreDestinatario, "http://localhost:8080/api/usuarios/verificar?uuid=" + UUIDString
      );
      mensaje.setText(text);
      javaMailSender.send(mensaje);
  }
  
  public void enviarEmailConNuevoUuidParaVerificarEmail(String nombreDestinatario, String direccionEmail, String UUIDString) {
      SimpleMailMessage mensaje = new SimpleMailMessage();
      mensaje.setTo(direccionEmail);
      mensaje.setSubject("[nombrePagina.com] Verificación de Cuenta");

      // Personalizar el mensaje para saludar al usuario por su nombre
      String text = String.format(
              "Hola de nuevo %s,\n\nPara verificar tu cuenta, haz clic en el siguiente enlace: %s\n\nSaludos,\nEl Equipo",
              nombreDestinatario, "http://localhost:8080/api/usuarios/verificar?uuid=" + UUIDString
      );
      mensaje.setText(text);
      javaMailSender.send(mensaje);
  }
  
//Ponemos transactional para evitar el error El error could not initialize proxy - no Session.
  @Transactional
  public void enviarEmailPedido(String nombreDestinatario, String direccionEmail, Pedido pedido) throws MessagingException {
	  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	  Pedido pedidoGuardado = pedidoService.encontrarPorId(pedido.getIdPedido());
      List<LineaFacturacion> lineasFacturacion = pedidoGuardado.getLineasFacturacion();
      StringBuilder lineasHtml = new StringBuilder();

      for (LineaFacturacion linea : lineasFacturacion) {
          lineasHtml.append(String.format("""
              <tr>
                <td style="text-align: center">%s</td>
                <td>%s</td>
                <td style="text-align: center;">%s</td>
                <td style="text-align: right;">%.2f</td>
                <td style="text-align: right;">%.2f</td>
              </tr>
              """,
              linea.getProducto().getIdProducto(),
              linea.getProducto().getNombre(),
              linea.getCantidad(),
              linea.getPrecioUnitario(),
              linea.getPrecioLinea()
          ));
      }

      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      mimeMessageHelper.setTo(direccionEmail);
      mimeMessageHelper.setSubject("[nombrePagina.com] Muchas gracias por tu pedido");
      mimeMessageHelper.setText("""
          <div>
            <p>Hola %s</p>
            <p>¡Muchas gracias por comprar en Nombre Pagina! Estamos haciendo todo lo posible para que tu pedido te llegue lo antes posible. Con este correo te enviamos la confirmación de la compra.</p>
            <table border="1" cellpadding="5" cellspacing="0" style="border-collapse: collapse;">
              <tr>
                <td>idPedido</td><td>%s</td>
              </tr>
              <tr>
                <td>Fecha pedido</td><td>%s</td>
              </tr>
              <tr>
                <td>Método envío</td><td>%s</td>
              </tr>
            </table>
            <br>
            <table border="1" cellpadding="5" cellspacing="0" style="border-collapse: collapse;">
              <tr>
                <td>Id producto</td><td>Nombre</td><td>Cantidad</td><td>Precio unitario (€)</td><td>Precio total (€)</td>
              </tr>
              %s
              <tr>
                <td colspan="4" style="text-align: right;">Gastos de envío</td><td style="text-align: right;">%.2f</td>
              </tr>
              <tr>
                <td colspan="4" style="text-align: right;">Total (IVA incl.)</td><td style="text-align: right; background-color: #D9EFFF;"><strong>%.2f</strong></td>
              </tr>
            </table>
            <p>Lo enviaremos a la siguiente dirección</p>
            <table border="1" cellpadding="5" cellspacing="0" style="border-collapse: collapse;">
              <tr>
                <td>Nombre y apellidos</td><td>%s %s</td>
              </tr>
              <tr>
                <td>Dirección</td><td>%s</td>
              </tr>
              <tr>
                <td>N.º teléfono móvil</td><td>%s</td>
              </tr>
            </table>
            <p>¿Alguna pregunta?</p>
            <p>Utiliza para ello nuestras opciones de contacto en <a href="https://www.nombrepagina.com/servicio/contacto">www.nombrepagina.com/servicio/contacto</a>.</p>
            <p>Saludos desde nombreCiudad. El equipo de la tienda de nombreTienda</p>
            <p>P. D.: Ten en cuenta que este correo electrónico se ha generado automáticamente. Si deseas enviar una respuesta, dirígete directamente al servicio de atención al cliente.</p>
          </div>
          """.formatted(
          nombreDestinatario,
          pedido.getIdPedido(),
          pedido.getFechaPedido().format(formatter),
          pedido.getMetodoEnvio(),
          lineasHtml.toString(),
          pedido.getGastosEnvio(),
          pedido.getPrecioTotal(),
          pedido.getNombre(),
          pedido.getApellidos(),
          pedido.getDireccion(),
          pedido.getNumeroTelefonoMovil()
      ), true);

      javaMailSender.send(mimeMessage);
  }
}
