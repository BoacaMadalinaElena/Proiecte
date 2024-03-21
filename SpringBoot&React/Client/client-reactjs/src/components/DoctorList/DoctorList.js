import React, { useEffect, useState } from "react";
import './DoctorList.css';
import { useNavigate } from 'react-router-dom';

const DoctorList = () => {
    const navigate = useNavigate();
    const [list, setList] = useState([]);
    // echivalent componentDidMount pentru functii
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
                console.log(data);
                setList(data);
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
        // nu se primeste nimic
            .then((response) => "" )
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
            <br></br>
            <div id="personalData">
                <h2>În cadrul clinici noastre lucrează următori medici:</h2>
                <table>
                    <thead>
                        <tr>
                            <th>Nume</th>
                            <th>Prenume</th>
                            <th>Email</th>
                            <th>Specializare</th>
                        </tr>
                    </thead>
                    <tbody>
                        {list.map((physician) => (
                            <tr key={physician.id}>
                                <td>{physician.last_name}</td>
                                <td>{physician.first_name}</td>
                                <td>{physician.email}</td>
                                <td>{physician.specialization}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div >
    );
};

export default DoctorList;
