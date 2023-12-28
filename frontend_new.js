document.addEventListener('DOMContentLoaded', () => {
    let registrationForm = document.querySelector('#registrationForm');

    registrationForm.addEventListener('submit', function (e) {
        e.preventDefault();
        // Get form details
        const formData = {
            name: document.querySelector('#patient-name').value,
            id: document.querySelector('#patient-id').value,
            contact: document.querySelector('#contact').value,
            age: document.querySelector('#age').value,
            covid: document.querySelector('#covidStatus').value,
            address: document.querySelector('#address').value
        };

        // Print form details to console
        console.log('Form Details:',formData);

        // Fetch request
        fetch('http://localhost:8080/api/registerPatient', {
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
            console.log('Success:', data);
            alert('Patient registered successfully');
            document.querySelector('#registrationForm').reset();
        })
        .catch(error => {
            console.error('Error:', error.message);
        });
       });
});
