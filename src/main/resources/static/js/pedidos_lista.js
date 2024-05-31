function filtrarPorEstado() {
    var estado = document.getElementById("estadoSelect").value;
    var url = "/pedidos";
    if (estado) {
        url += "/filtrar?estado=" + estado;
    }
    window.location.href = url;
}