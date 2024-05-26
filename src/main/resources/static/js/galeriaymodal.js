let numeroImagenAlCargar = 0;
    let imagenAlCargar;
    let imagenSeleccionada = null;
    let diapositivas;
    let imgText;
    let divModal;
    let imagenDelModal;
    let divConTexto;
    let spanDeCierreDelModal;
    let lupa;

    window.onload = function() {
      // Obtener la segunda imagen en miniatura
      divFila = document.querySelector(".divFila");
      imagenAlCargar = document.querySelectorAll('.imagenDeSlide')[numeroImagenAlCargar];
      diapositivas = document.querySelectorAll('.imagenDeSlide');
      lupa = document.querySelector('.lupa');
      imgText = document.getElementById("imgtext");
      divModal = document.querySelector(".divModal");
      imagenDelModal = document.querySelector(".imagenDelModal");
      divConTexto = document.getElementById("divConTexto");
      spanDeCierreDelModal = document.getElementsByClassName("spanDeCierreDelModal")[0];

      if (imagenAlCargar) {
        mostrarImagen(imagenAlCargar);
      }

      // Agregar el evento click a divDeImagenExpandida
      document.querySelector('.divDeImagenExpandida').onclick = abrirModal;
      imagenExpandida.onmouseover = mostrarLupa;
      imagenExpandida.onmouseout = ocultarLupa;

      // Cuando el usuario clica en <span> (x), se cierra el modal
      spanDeCierreDelModal.onclick = ocultarModal;
    };

    function mostrarImagen(imagen) {
      // Restablecer borde de todas las imágenes
      for (var i = 0; i < diapositivas.length; i++) {
        diapositivas[i].style.backgroundColor = "white"; // Restablecer a fondo blanco
      }

      imagenExpandida.src = imagen.src;
      imagenExpandida.alt = imagen.alt;
      imgText.innerHTML = imagen.alt;
      imagenExpandida.parentElement.style.display = "block";

      // Establecer borde de la imagen seleccionada
      imagen.style.backgroundColor = "grey";
      imagenSeleccionada = imagen;
    }

    function mostrarLupa() {
      lupa.style.display = 'block'
    }

    function ocultarLupa() {
      lupa.style.display = 'none';
    }

    function backgroundDeImagenAGris(imagen) {
      if (imagen !== imagenSeleccionada) {
        imagen.style.backgroundColor = "grey";
      }
    }

    function backgroundDeImagenABlanco(imagen) {
      if (imagen !== imagenSeleccionada) {
        imagen.style.backgroundColor = "white";
      }
    }

    function ocultarModal() {
      divModal.style.display = "none";
    }

    function abrirModal() {
      divModal.style.display = "block";
      imagenDelModal.src = imagenExpandida.src;
      divConTexto.innerHTML = imgText.innerHTML;
    }