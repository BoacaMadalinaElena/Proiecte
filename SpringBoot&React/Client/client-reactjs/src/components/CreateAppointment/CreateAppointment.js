import React, { useEffect, useState } from "react";
import './CreateAppointment.css';
import { useNavigate } from 'react-router-dom';
import Select from 'react-select';

const CreateAppointment = () => {
    const navigate = useNavigate();

    const [state, setState] = useState({ id_patient: 0, id_physician: 0, dateTime: '', dataDate: '', date: '', status: null });
    const [options, SetOptions] = useState({ list: [] });
    const [error, SetError] = useState("");

    const handleDoctorChange = selectedOption => {
        let oldState = state;
        oldState.id_physician = selectedOption.value;
        setState(oldState);
    };

    useEffect(() => {
        let token = getCookie("Token");
        fetch('http://localhost:8086/api/pos/physicians/short', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                Authorization: token
            },
        })
            .then((response) => response.json())
            .then((data) => {
                let myOptions = data.map(item => ({
                    value: item.id,
                    label: `${item.first_name} ${item.last_name} - ${item.specialization}`
                }));
                SetOptions(myOptions);
            })
            .catch((error) => {
                console.log(error);
            });
    }, []);

    const handleClick = () => {
        let token = getCookie("Token");
        fetch('http://localhost:8086/api/pos/idm/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                Authorization: token
            },
        })
            .then((response) => response.json())
            .then((data) => {
                console.log(data);
                deleteCookie("Token");
                deleteCookie("Id");
                deleteCookie("Role");
                alert("Delogarea sa făcut cu succes!");
                navigate("/login");
            })
            .catch((error) => {
                console.log(error);
            });
    };

    const getCookie = (name) => {
        const cookies = document.cookie.split(';');
        for (let cookie of cookies) {
            const [cookieName, cookieValue] = cookie.trim().split('=');
            if (cookieName === name) {
                return cookieValue;
            }
        }
        return null;
    };

    // pentru stergere data invalida
    const deleteCookie = (name) => {
        document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
    }

    const handleSubmit = async (event) => {
        event.preventDefault();

        try {
            let id = getCookie("Id");
            let token = getCookie("Token");

            let updatedState = {
                id_patient: id,
                id_physician: state.id_physician,
                date: state.dataDate + " " + state.dateTime,
                status: state.status
            };

            // Procesare
            let response = await fetch(`http://localhost:8086/api/pos/appointments/${id}/${updatedState.id_physician}/${updatedState.date}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token
                },
                body: JSON.stringify(updatedState),
            });

            if (response.status === 401) {
                SetError("Acces neautorizat!");
                throw new Error("Acces neautorizat!");
            }
            if (response.status === 204) {
                SetError("Există deja o programare la această dată!");
                throw new Error("Există deja o programare la această dată!");
            }
            if (response.status === 406) {
                let data = await response.json();
                console.log("Raspuns 406: " + data.message)
                if (data.message !== undefined) {
                    SetError(data.message)
                }
                else {
                    SetError(data.error);
                }
                console.log("Status 406");
                return;
            }
            if (response.status === 409) {
                let data = await response.json();
                console.log("Raspuns 409: " + data.message)
                if (data.message !== undefined) {
                    SetError(data.message)
                }
                else {
                    SetError(data.error);
                }
                return;
            }
            if (response.status !== 201) {
                SetError("Eroare la server sau date invalide!");
                throw new Error("Eroare la server!");
            }

            console.log("Status 201");

            let data = await response.json();
            console.log('Response from server:', data);
            SetError("");
            alert("Programare adăugată cu succes!")

        } catch (error) {
            console.error('Eroare:', error);
        }
    };


    const handleDateChange = (e) => {
        const selectedDate = e.target.value.split(" ")[0];
        let oldState = state;
        oldState.dataDate = selectedDate;
        setState(oldState)
    };


    const handleTimeChange = (e) => {
        let oldState = state;
        oldState.dateTime = e.target.value;
        setState(oldState);
    };

    return (
        <div>
            <div id="navbar">
                <ul>
                <li><a href="/homePatient">Pacient</a></li>
                    <li><a href="/doctorList">Listă doctori</a></li>
                    <li><a href="/createAppointment">Creare programare</a></li>
                    <li><a href="/appointmentsList">Listă de programării</a></li>
                    <li><a href="/istoricMedicalPacient">Istoric consultații</a></li>
                </ul>
                <button onClick={handleClick}>Logout</button>
            </div>
            <h2>
                Vă puteți programa la unul din medici noștrii, ora programării trebuie să fie cu minutele 00, 15, 30 sau 45. O programare durează 15 minute.
            </h2>
            <h2>
                Pentru lista de doctori puteți accesa tab-ul listă doctori.
            </h2>
            <br></br><br></br><br></br><br></br><br></br>
            <div id="personalData">
                <form onSubmit={handleSubmit}>
                    <h2>Creați o programare:</h2>
                    <label>Doctor:</label>
                    <Select
                        id="comboBox"
                        options={options}
                        onChange={handleDoctorChange}
                        isSearchable
                        placeholder="Selectează..."
                    />
                    <br />
                    <label>Data programării:</label>
                    <input
                        type="date"
                        onChange={handleDateChange}
                    />
                    <label>Ora programării:</label>
                    <input
                        type="time"
                        onChange={handleTimeChange}
                    />
                    <br />
                    <p>{error}</p>
                    <input type="submit" value="Creare" />
                </form>
            </div>
        </div>
    );
};

export default CreateAppointment;
