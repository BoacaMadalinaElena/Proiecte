import React, { useEffect, useState } from "react";
import './IstoricMedicalPacient.css';
import { useNavigate } from 'react-router-dom';

const IstoricMedicalPacient = () => {
    const navigate = useNavigate();
    const [list, setList] = useState([]);
    const [listPhysicians, setListPhysicians] = useState([]);

    useEffect(() => {
        let token = getCookie("Token");
        let id = getCookie("Id");
        fetch('http://localhost:8086/api/pos/consultation/patient/' + id + '', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                Authorization: token
            },
        })
            .then((response) => response.json())
            .then((data) => {
                setList(data);
            })
            .catch((error) => {
                console.log(error);
            });
    }, []);


    const handleClick = () => {
        let token = getCookie("Token");
        fetch('http://localhost:8086/api/medical_office/idm/logout', {
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
                    Istoricul consultațiilor dumneavoastră cu diagnosticul si analizele este:
                </h1>
                <table>
                    <thead>
                        <tr>
                            <th>Data</th>
                            <th>Diagnostic</th>
                            <th>Investigații(analize)</th>
                        </tr>
                    </thead>
                    <tbody>
                        {list.map((consultation, index) => (
                            <tr >
                                <td>{consultation.date}</td>
                                <td>{consultation.diagnostic}</td>
                                <td>
                                    <ul>
                                        {consultation.investigations.map((investigation, i) => (
                                            <li class="list" >
                                                Nume: {investigation.name}, Timp efectuare: {investigation.days}, Rezultat: {investigation.result}
                                            </li>
                                        ))}
                                    </ul>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default IstoricMedicalPacient;
