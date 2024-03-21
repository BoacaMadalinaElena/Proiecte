import React, { useEffect, useState } from "react";
import './ConsultationDoctorComponent.css';
import { useNavigate, useLocation } from 'react-router-dom';

const ConsultationDoctorComponent = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [list, setList] = useState([]);
    const idPatient = new URLSearchParams(location.search).get('idPatient');
    const idDoctor = new URLSearchParams(location.search).get('idDoctor');
    const date = new URLSearchParams(location.search).get('date');

    useEffect(() => {
        var obiectData = new Date(date);
        var an = obiectData.getFullYear();
        var luna = ('0' + (obiectData.getMonth() + 1)).slice(-2);
        var zi = ('0' + obiectData.getDate()).slice(-2);
        var ora = ('0' + obiectData.getHours()).slice(-2);
        var minute = ('0' + obiectData.getMinutes()).slice(-2);

        var dateFormat = an + "-" + luna + "-" + zi + " " + ora + ":" + minute;

        let token = getCookie("Token");
        fetch('http://localhost:8086/api/pos/consultation/' + idPatient + '/' + idDoctor + '/' + dateFormat, {
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

    const redirectToConsultation = () => {
        navigate(`/createConsulation?idPatient=${idPatient}&idDoctor=${idDoctor}&date=${date}`);
    };

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
                    Consultațiile pentru această programare sunt:
                </h1>
                <br></br> <br></br>
                <button onClick={redirectToConsultation}>
                    Crează consultație pentru această programare
                </button>
                <br></br> <br></br> <br></br>
                <table>
                    <thead>
                        <tr>
                        <th>Dată</th>
                            <th>Diagnostic</th>
                            <th>Investigații</th>
                        </tr>
                    </thead>
                    <tbody>
                        {Array.isArray(list) ? list.map((consultation) => (
                            <tr>
                                 <td>{consultation.date}</td>
                                <td>{consultation.diagnostic}</td>
                                <td>
                                    {consultation.investigations.map((investigation, index) => (
                                        <p id="listInvestigations">
                                            {`Investigație ${index + 1}:Nume ${investigation.name},Durată ${investigation.days} zile, Rezultat: ${investigation.result}`}
                                            <br />
                                        </p>
                                    ))}
                                </td>
                            </tr>
                        )) : ""}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default ConsultationDoctorComponent;
