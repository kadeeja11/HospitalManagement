document.addEventListener('DOMContentLoaded', () => {
    let adminLoginForm = document.querySelector('#adminLoginForm');
    let patientsList = document.getElementById('patientsList');
    let editPatientForm = document.getElementById('editPatientForm');

    // Event listener for admin login form
    adminLoginForm.addEventListener('submit', function (e) {
        e.preventDefault();

        // Get admin id
        const adminId = document.querySelector('#admin-id').value;

        // Check if the entered admin id is correct
        if (adminId === '100') {
            // If admin id is correct, fetch and display patient data
            fetchPatientsData();
            window.location.href = 'C:/Users/junai/Documents/Kajji/DigiLedge/hospitalmanagement/admin_after_login.html';
        } else {
            alert('Invalid Admin ID');
        }
    });

    // Fetch and display patient data
    function fetchPatientsData() {
        fetch('http://localhost:8080/api/getPatients')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                console.log('Data received:', data);
                // Process the data as needed
            })
            .catch(error => {
                console.error('Error during fetch:', error);
            });
    }
});


