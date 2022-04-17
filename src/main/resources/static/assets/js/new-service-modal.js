// Get the newServiceModal
let newServiceModal = document.getElementById("servicesModal");

// Get the button that opens the newServiceModal
let btn = document.getElementById("newModalButton");

// Get the <span> element that closes the newServiceModal
let span = document.getElementsByClassName("closeNew")[0];

// When the user clicks on the button, open the newServiceModal
btn.onclick = function() {
    newServiceModal.style.display="block";
}

// When the user clicks on <span> (x), close the newServiceModal
span.onclick = function() {
    newServiceModal.style.display = "none";
}

// When the user clicks anywhere outside of the newServiceModal, close it
window.onclick = function(event) {
    if (event.target === newServiceModal) {
        newServiceModal.style.display = "none";
    }
}