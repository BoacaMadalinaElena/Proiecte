import React, { useEffect, useState } from "react";
import './AppointmentsListDoctor.css';
import { useNavigate, useLocation } from 'react-router-dom';

const AppointmentsListDoctor = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [list, setList] = useState([]);
    const idPatient = new URLSearchParams(location.search).get('id');

    const [selectedOption, setSelectedOption] = useState('');

    const handleChange = async (event, appointment) => {
        setSelectedOption(event.target.value);
        let id = getCookie("Id");
        let token = getCookie("Token");
        appointment.status = event.target.value;
        var obiectData = new Date(appointment.date);
        var an = obiectData.getFullYear();
        var luna = ('0' + (obiectData.getMonth() + 1)).slice(-2);
        var zi = ('0' + obiectData.getDate()).slice(-2);
        var ora = ('0' + obiectData.getHours()).slice(-2);
        var minute = ('0' + obiectData.getMinutes()).slice(-2);
   
        var dateFormat = an + "-" + luna + "-" + zi + " " + ora + ":" + minute;
        appointment.date = dateFormat;
        try {
            let response = await fetch(`http://localhost:8086/api/pos/appointments/${idPatient}/${id}/${dateFormat}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token
                },
                body: JSON.stringify(appointment),
            });
    
            if (response.status === 401) {
                throw new Error("Acces neautorizat!");
            }
            if (response.status !== 204) {
                throw new Error("Eroare la server!");
            }
            fetchData();
            alert("Status schimbat cu succes");
          
        } catch (error) {
            console.error("Eroare:", error);
            alert(error)
        }
    };
    
    useEffect(() => {
        let token = getCookie("Token");
        let id = getCookie("Id");
        console.log("Medic id: " + id)
        fetch('http://localhost:8086/api/pos/appointments/patients/' + idPatient + '/physicians', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                Authorization: token
            },
        })
            .then((response) => response.json())
            .then((data) => {
                const filteredArray = data.filter(obj => obj.id_physician == id);
                setList(filteredArray);
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

    const handleAddConsultation = async (e) => {
        const idPatient = e.id_patient;
        const idDoctor = e.id_physician;
        const date = e.date;
        navigate(`/consultationDoctorComponent?idPatient=${idPatient}&idDoctor=${idDoctor}&date=${date}`);
    }

    return (
        <div>
            <div id="navbar">
                <ul>
                    <li><a href="/homeDoctor">Home doctor</a></li>
                    <li><a href="/patientsDoctor">Pacienți dumneavoastră</a></li>
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
                            <th>Data</th>
                            <th>Status</th>
                            <th>Acțiuni</th>
                        </tr>
                    </thead>
                    <tbody>
                        {Array.isArray(list) ? list.map((appointment) => (
                            <tr key={appointment.id}>
                                <td>{appointment.date}</td>
                                <td>
                                    <button onClick={() => handleAddConsultation(appointment)}>Gestionează consultații pentru programare</button>
                                </td>
                                <td>
                                    <td>{appointment.status != null ? appointment.status : "Încă nu există un status"}</td>
                                    <td>
                                        <select value={selectedOption} onChange={(event) => handleChange(event, appointment)}>
                                            <option value="null">Nici unul</option>
                                            <option value="honored">Onorată</option>
                                            <option value="not presented">Neefectuată</option>
                                            <option value="cancelled">Anulată</option>
                                        </select>
                                    </td>
                                </td>
                            </tr>
                        )) : ""}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AppointmentsListDoctor;
