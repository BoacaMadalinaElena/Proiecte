import React, { useEffect, useState } from "react";
import './AppointmentsList.css';
import { useNavigate } from 'react-router-dom';

const AppointmentsList = () => {
    const navigate = useNavigate();
    const [list, setList] = useState([]);
    const [listPhysicians, setListPhysicians] = useState([]);

    useEffect(() => {
        let token = getCookie("Token");
        let id = getCookie("Id");
        fetch('http://localhost:8086/api/pos/appointments/patients/' + id + '/physicians', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                Authorization: token
            },
        })
            .then((response) => response.json())
            .then((data) => {
                console.log(data);
                setList(data);
            })
            .catch((error) => {
                console.log(error);
            });

        fetch('http://localhost:8086/api/pos/physicians/short', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                Authorization: token
            },
        })
            .then((response) => response.json())
            .then((data) => {
                console.log(data);
                setListPhysicians(data);
            })
            .catch((error) => {
                console.log(error);
            });
    }, []);

    const fetchData = () => {
        let token = getCookie("Token");
        let id = getCookie("Id");
        fetch('http://localhost:8086/api/pos/appointments/patients/' + id + '/physicians', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                Authorization: token
            },
        })
            .then((response) => response.json())
            .then((data) => {
                console.log(data);
                setList(data);
            })
            .catch((error) => {
                console.log(error);
            });

        fetch('http://localhost:8086/api/pos/physicians/short', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                Authorization: token
            },
        })
            .then((response) => response.json())
            .then((data) => {
                console.log(data);
                setListPhysicians(data);
            })
            .catch((error) => {
                console.log(error);
            });
    }
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

    const handleDelete = async (e) => {
        try {
            let id = getCookie("Id");
            let token = getCookie("Token");
            let dateObj = new Date(e.date);
            let formattedDate = `${dateObj.getFullYear()}-${(dateObj.getMonth() + 1).toString().padStart(2, '0')}-${dateObj.getDate().toString().padStart(2, '0')} ${dateObj.getHours().toString().padStart(2, '0')}:${dateObj.getMinutes().toString().padStart(2, '0')}`;

            let updatedState = {
                id_patient: e.id_patient,
                id_physician: e.id_physician,
                date: formattedDate,
                status: "cancelled"
            };

            // Procesare
            let response = await fetch(`http://localhost:8086/api/pos/appointments/${id}/${updatedState.id_physician}/${updatedState.date}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token
                },
                body: JSON.stringify(updatedState),
            });

            if (response.status === 204) {
                alert("Programare ștearsă cu succes!")
                fetchData();
            }
            if (response.status === 401) {
                alert("Eroare de autentificare! Vă rugăm să vă reconectați.")
            }
            if (response.status === 403) {
                alert("Eroare de autentificare! Încercați să accesați resurse la care nu aveți acces.")
            }
        } catch (error) {
            console.error('Eroare:', error);
        }
    }

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
            <div id="personalData">
                <h1>
                    Programările dumneavoastră sunt:
                </h1>
                <table>
                    <thead>
                        <tr>
                            <th>Medic</th>
                            <th>Data</th>
                            <th>Status</th>
                            <th>Acțiuni</th>
                        </tr>
                    </thead>
                    <tbody>
                        {Array.isArray(list) ? list.map((appointment) => (
                            <tr key={appointment.id}>
                                <td>
                                    {listPhysicians.find((ob) => ob.id === appointment.id_physician).first_name}{' '}
                                    {listPhysicians.find((ob) => ob.id === appointment.id_physician).last_name}
                                </td>
                                <td>{appointment.date}</td>
                                <td>{appointment.status != null ? appointment.status : "Încă nu există un status"}</td>
                                <td>
                                    <button onClick={() => handleDelete(appointment)}>Ștergere</button>
                                </td>
                            </tr>
                        )) : ""}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AppointmentsList;
