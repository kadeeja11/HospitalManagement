document.addEventListener('DOMContentLoaded', () => {
    let registrationForm = document.querySelector('#loginForm');

    registrationForm.addEventListener('submit', function (e) {
        e.preventDefault();
        // Get form details
        const formData = {
            id: document.querySelector('#patient-id').value, // Updated ID to match HTML
            contact: document.querySelector('#contact').value
        };

        // Print form details to console
        console.log('Form Details:', formData);

        // Fetch request
        // needs to be changed
        fetch('http://localhost:8080/api/checkPatient', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData),
        })
        .then(response => {
            console.log('Full response:', response);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data => {
            console.log('Login Success:', data);
            alert(data); // Show the response message in an alert
        })
        .catch(error => {
            console.error('Error:', error.message);
        });
    });
});
