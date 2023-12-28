document.addEventListener('DOMContentLoaded', () => {
    let data; // Declare a variable to store patient data

    // Fetch and display patient data on the admin_after_login.html page
    fetchPatientsData();

    function fetchPatientsData() {
        fetch('http://localhost:8080/api/getPatients')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(patientsData => {
                console.log('Data received:', patientsData);
                data = patientsData; // Assign the data to the variable
                displayPatients(data);
            })
            .catch(error => {
                console.error('Error during fetch:', error);
            });
    }

    function displayPatients(data) {
        const patientsListContainer = document.getElementById('patientsListContainer');
        patientsListContainer.innerHTML = ''; // Clear previous data

        if (data.length === 0) {
            patientsListContainer.innerHTML = '<p>No patients found</p>';
        } else {
            const table = document.createElement('table');
            table.innerHTML = `
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Contact</th>
                    <th>Age</th>
                    <th>COVID Status</th>
                    <th>Address</th>
                    <th>Action</th>
                </tr>
            `;

            data.forEach(patient => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${patient.id}</td>
                    <td>${patient.name}</td>
                    <td>${patient.contact}</td>
                    <td>${patient.age}</td>
                    <td>${patient.covid}</td>
                    <td>${patient.address}</td>
                    <td>
                        <button class="btn btn-edit" onclick="editPatient(${patient.id})">Edit</button>
                        <button class="btn btn-remove" onclick="removePatient(${patient.id})">Remove</button>
                    </td>
                `;
                table.appendChild(row);
            });

            patientsListContainer.appendChild(table);
        }
    }

    function createInput(type, id, value) {
        const input = document.createElement('input');
        input.type = type;
        input.id = id;
        input.value = value;
        return input;
    }

    window.editPatient = function (patientId) {
        console.log('Editing patient with ID:', patientId);
        // Fetch patient details for editing
        fetch(`http://localhost:8080/api/editPatient/${patientId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(patient => {
                // Create a form for editing
                const editForm = document.createElement('form');
                editForm.id = 'editForm';

                // Define input fields
                const nameInput = createInput('text', 'edit-name', patient.name);
                const contactInput = createInput('text', 'edit-contact', patient.contact);
                const ageInput = createInput('text', 'edit-age', patient.age);
                const covidInput = createInput('text', 'edit-covid-status', patient.covid);
                const addressInput = createInput('text', 'edit-address', patient.address);

                // Append input fields to the form
                editForm.appendChild(nameInput);
                editForm.appendChild(contactInput);
                editForm.appendChild(ageInput);
                editForm.appendChild(covidInput);
                editForm.appendChild(addressInput);

                // Add a submit button
                const submitButton = document.createElement('button');
                submitButton.type = 'button';
                submitButton.textContent = 'Save';
                submitButton.onclick = () => savePatientDetails(patientId);
                editForm.appendChild(submitButton);

                // Show the edit form
                document.getElementById('patientsListContainer').innerHTML = '';
                document.getElementById('patientsListContainer').appendChild(editForm);
            })
            .catch(error => {
                console.error('Error during fetch:', error);
            });
    };

    function savePatientDetails(patientId) {
        // Fetch edited patient details
        const editedPatient = {
            id: patientId,
            name: document.getElementById('edit-name').value,
            contact: document.getElementById('edit-contact').value,
            age: document.getElementById('edit-age').value,
            covid: document.getElementById('edit-covid-status').value,
            address: document.getElementById('edit-address').value
        };

        // Send a request to update the patient details
        fetch('http://localhost:8080/api/editPatient', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify(editedPatient),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            // Refresh the patient data after editing
            fetchPatientsData();
        })
        .catch(error => {
            console.error('Error:', error.message);
        });
    }

    window.removePatient = function (patientId) {
        // Show a confirmation dialog
        const confirmDelete = confirm('Are you sure you want to remove this patient?');

        if (confirmDelete) {
            // Send a request to remove the patient
            fetch(`http://localhost:8080/api/removePatient/${patientId}`, {
                method: 'DELETE',
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                // Refresh the patient data after removal
                fetchPatientsData();
            })
            .catch(error => {
                console.error('Error:', error.message);
            });
        }
    };
});
