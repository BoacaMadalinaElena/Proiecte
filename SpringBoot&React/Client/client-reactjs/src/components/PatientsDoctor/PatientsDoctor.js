import React, { useEffect, useState } from "react";
import "./PatientsDoctor.css"
import { useNavigate } from 'react-router-dom';

const PatientsDoctor = () => {
    const navigate = useNavigate();
    const [listPatients, SetListPatients] = useState([]);

    useEffect(() => {
        console.log("componentDidMount 2");
        let token = getCookie("Token");
        let myPatients = [];
        // lista pacientilor utilizatorului
        fetch('http://localhost:8086/api/pos/appointments/physicians/' + getCookie("Id") + '/patientsId', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                Authorization: token
            },
        })
            .then((response) => response.json())
            .then((data) => {
                myPatients = data;
                fetch('http://localhost:8086/api/pos/patients/listPatients', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: token
                    },
                    body: JSON.stringify({ list: myPatients }),
                })
                    .then((response) => response.json())
                    .then((data) => {
                        console.log("Pacientii mei full: " + data);
                        SetListPatients(data)
                    })
                    .catch((error) => {
                        console.log(error);
                    });
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

    const handeShow = async (e) => {
        let id= e.id;
        navigate(`/appointmentsListDoctor?id=${id}`);
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
            <h1>Pacienți dumneavoastră sunt:</h1>
            <table>
                <thead>
                    <tr>
                        <th>Prenume</th>
                        <th>Nume</th>
                        <th>Email</th>
                        <th>Telefon</th>
                        <th>Acțiuni</th>
                    </tr>
                </thead>
                <tbody>
                    {listPatients.map((patient) => (
                        <tr key={patient.id}>
                            <td>{patient.firstName}</td>
                            <td>{patient.lastName}</td>
                            <td>{patient.email}</td>
                            <td>{patient.telephone}</td>
                            <td>
                                <button onClick={() => handeShow(patient)}>Vizualizare programări</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );


}
export default PatientsDoctor;