
function toggleDescuentoField() {
    var tipoDescuento = document.getElementById('tipoDescuentoLS').value;
    var descuentoField = document.getElementById('descuentoLI');
    if (tipoDescuento === 'porcentual' || tipoDescuento === 'absoluto') {
        descuentoField.disabled = false;
    } else {
        descuentoField.disabled = true;
        descuentoField.value = ''; // Opcional: limpia el campo cuando se deshabilita
    }
}

document.addEventListener('DOMContentLoaded', function() {
    toggleDescuentoField(); // Invoca la función al cargar la página
    document.getElementById('tipoDescuentoLS').addEventListener('change', toggleDescuentoField);
});