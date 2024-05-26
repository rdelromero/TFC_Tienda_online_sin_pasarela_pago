const indiceTabAlCargar = 0;
        let tablinks;
        let tabcontent;

        document.addEventListener('DOMContentLoaded', function() {
            tablinks = document.getElementsByClassName("tablinks");
            tabcontent = document.getElementsByClassName("tabcontent");
            tablinks[indiceTabAlCargar].className += " activo";
            tabcontent[indiceTabAlCargar].style.display = "block";
        });

        function abrirCampo(evento, nombreCampo) {
            let i;
            // Remueve la clase "activo" de todos los elementos con la clase CSS "tablinks".
            for (i = 0; i < tablinks.length; i++) {
                tablinks[i].className = tablinks[i].className.replace(" activo", "");
            }
            // Oculta todo el contenido de las pestañas.
            for (i = 0; i < tabcontent.length; i++) {
                tabcontent[i].style.display = "none";
            }
            // Muestra el elemento con el ID igual al valor de nombreCampo.
            document.getElementById(nombreCampo).style.display = "block";
            // Agrega la clase "activo" al elemento que desencadenó el evento de clic.
            evento.currentTarget.className += " activo";
        }