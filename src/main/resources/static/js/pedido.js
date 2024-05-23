document.addEventListener('DOMContentLoaded', function () {
    var select = document.getElementById('estadoLS');
    var currentState = select.getAttribute('data-current-state');  // Asumiendo que pasas el estado actual como un atributo de datos

    Array.from(select.options).forEach(function(option) {
        // Regla general: primero deshabilitar todas las opciones
        option.disabled = true;

        // Habilitar opciones según el estado actual
        switch(currentState) {
            case 'pendiente':
                // Si es pendiente, habilitar solo pendiente y cancelado
                if (option.value === 'pendiente' || option.value === 'cancelado') {
                    option.disabled = false;
                }
                break;
            case 'enviado':
                // Si es enviado, habilitar solo enviado
                if (option.value === 'enviado') {
                    option.disabled = false;
                }
                break;
            case 'entregado':
                // Si es entregado, habilitar solo entregado
                if (option.value === 'entregado') {
                    option.disabled = false;
                }
                break;
            case 'cancelado':
                // Si es cancelado, habilitar solo pendiente y cancelado
                if (option.value === 'pendiente' || option.value === 'cancelado') {
                    option.disabled = false;
                }
                break;
        }
    });
});