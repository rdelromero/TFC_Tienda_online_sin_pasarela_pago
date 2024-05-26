
    let numeroImagenAlCargar = 0;
    var imagenSeleccionada = null;
    var imagenAlCargar;
    let imagenExpandida;
    let divDeTextoAlt = document.getElementById("divDeTextoAlt");
    //El código se ha probado para numeroDiapositivasVisibles = 5 y desplazamiento 1, 2, 3, 4, 5
    //El número de spans es igual al número de bloques.
    //desplazamiento=1 -> spans=19, 2->10, 3->7, 4->6, 5->5
    //Los comentarios que dan valores concretos de variables se han hecho para desplazamiento = 3.
    let indiceDiapositiva = 0;
    let numeroDiapositivasVisibles = 4;
    let desplazamiento = 1;
    let diapositivas;
    let numeroDiapositivas;
    let divDeImagenes;
    let sigu;
    let spans;
    let numeroBloques;

    document.addEventListener('DOMContentLoaded', function() {
      imagenAlCargar = document.querySelectorAll('.divDeImagenes img')[numeroImagenAlCargar];
      imagenExpandida = document.getElementById("imagenExpandida");
      divDeTextoAlt = document.getElementById("divDeTextoAlt");
      /********************************************************************************/
      diapositivas = document.querySelectorAll('.divDeImagenes img');
      numeroDiapositivas = diapositivas.length;
      divDeImagenes = document.querySelector(".divDeImagenes");
      sigu = document.querySelector(".sigu");
      prev = document.querySelector(".prev");
      
      if (diapositivas.length <= numeroDiapositivasVisibles) {
          numeroBloques = 1;
      } else {
          numeroBloques = 1 + Math.ceil((diapositivas.length - numeroDiapositivasVisibles) / desplazamiento);
      }
      
      const contenedorDeSpans = document.getElementById('contenedorDeSpans');

      for (let i = 0; i < numeroBloques; i++) {
            let span = document.createElement('span');
            span.setAttribute('onclick', `setYMostrarDiapositiva(${i * desplazamiento})`);
            contenedorDeSpans.appendChild(span);
      }

      spans = document.querySelectorAll(".contenedorDerecho span");

      setYMostrarDiapositiva(indiceDiapositiva);
      
      if (imagenAlCargar) {
        mostrarImagenSeleccionada(imagenAlCargar);
      }
    });

    function mostrarImagenSeleccionada(imagen) {

      // Restablecer borde de todas las imágenes
      for (var i = 0; i < diapositivas.length; i++) {
        diapositivas[i].style.backgroundColor = "white"; // Restablecer a fondo blanco
      }
      imagenExpandida.src = imagen.src;
      divDeTextoAlt.innerHTML = imagen.alt;
      imagen.style.backgroundColor = "cyan";
      imagenSeleccionada = imagen;
    }

    function colorearPadding(imagen) {
      if (imagen !== imagenSeleccionada) {
        imagen.style.backgroundColor = "cyan";
      }
    }

    function descolorearPadding(imagen) {
      if (imagen !== imagenSeleccionada) {
        imagen.style.backgroundColor = "white";
      }
    }

    function setYMostrarDiapositiva(n) {
      //Si el nº diapositiva es 0 o está entre 3 y 18:
      if ((n == 0) || (n>=0 && desplazamiento <= n && n<=numeroDiapositivas - numeroDiapositivasVisibles)) {
        indiceDiapositiva = n;
        irADiapositivaActual();
      }
      //Si el nº diapositiva está entre 1 y 2 entonces vamos a la diapositiva 3
      else if (n >= 1 && n <= desplazamiento-1) {
        indiceDiapositiva = desplazamiento;
        irADiapositivaActual();
      }
      // Si el nº diapositiva está entre 8 y 11 entonces vamos a la diapositiva 7
      else if (numeroDiapositivas - numeroDiapositivasVisibles < n  && n < numeroDiapositivas) {
        indiceDiapositiva = numeroDiapositivas - numeroDiapositivasVisibles;
        irADiapositivaActual();
      }
    }

    function setMasYMostrarDiapositiva() {
      if (0 <= indiceDiapositiva && indiceDiapositiva < numeroDiapositivas-numeroDiapositivasVisibles) {
        indiceDiapositiva += desplazamiento;
      }
      setYMostrarDiapositiva(indiceDiapositiva);
    }

    function setMenosYMostrarDiapositiva() {
      //Entre 0 y 18
      if (desplazamiento <= indiceDiapositiva && indiceDiapositiva < numeroDiapositivas-numeroDiapositivasVisibles) {
        indiceDiapositiva += -desplazamiento;
      }
      //Para 18
      else if (indiceDiapositiva == numeroDiapositivas-numeroDiapositivasVisibles) {
        let diapositivasSobrantes = numeroDiapositivas - (numeroDiapositivasVisibles+desplazamiento*Math.floor((numeroDiapositivas-numeroDiapositivasVisibles)/desplazamiento));
        if (diapositivasSobrantes > 0) {
          indiceDiapositiva += -diapositivasSobrantes;
        }
        else if (diapositivasSobrantes == 0) {
          indiceDiapositiva += -desplazamiento
        }
      }
      setYMostrarDiapositiva(indiceDiapositiva);
    }

    function irADiapositivaActual() {
      console.log(indiceDiapositiva);
      if (indiceDiapositiva==0) {
        desactivarAPrev();
      }
      if (indiceDiapositiva>0) {
        activarAPrev();
      }
      if (indiceDiapositiva<numeroDiapositivas-numeroDiapositivasVisibles) {
        activarASigu();
      }
      if (indiceDiapositiva==numeroDiapositivas - numeroDiapositivasVisibles) {
        desactivarASigu();
      }
      let porcentaje = -100*indiceDiapositiva/numeroDiapositivas;
      divDeImagenes.style.transform="translateY("+porcentaje+"%)";
      desactivarSpansActivarSpan();
    }

    function desactivarAPrev(){
      prev.removeAttribute("onclick");
      prev.style.color = "grey";
      prev.style.cursor = "default";
      prev.style.backgroundColor = 'transparent';
    }

    function desactivarASigu(){
      sigu.removeAttribute("onclick"); // Limpiar el atributo onclick
      sigu.style.color = "grey";
      sigu.style.cursor = "default";
      sigu.style.backgroundColor = 'transparent';
    }

    function activarAPrev() {
      prev.onclick = setMenosYMostrarDiapositiva;
      prev.style.color = "white";
      prev.style.cursor = "pointer";
      prev.style.backgroundColor = '';
    }

    function activarASigu() {
      sigu.onclick = setMasYMostrarDiapositiva;
      sigu.style.color = "white";
      sigu.style.cursor = "pointer";
      sigu.style.backgroundColor = '';
    }

    function desactivarSpansActivarSpan() {
      let n = Math.ceil(indiceDiapositiva/desplazamiento);
      for (i = 0; i < spans.length; i++) {
      spans[i].className = spans[i].className.replace(" activo", "");
    }
      spans[n].className += " activo";
    }

