import React, { useEffect, useState } from "react";
import './CreateConsutation.css';
import { useNavigate, useLocation } from 'react-router-dom';

const CreateConsutation = () => {
    const navigate = useNavigate();

    const [state, setState] = useState({
        id: null,
        id_patient: 0,
        id_physician: 0,
        date: "",
        diagnostic: "",
        investigations: [
        ]
    });
    const [newInvestigation, SetNewInvestigation] = useState({
        name: "",
        days: 0,
        result: ""
    });

    const [error, SetError] = useState("");
    const location = useLocation();

    const idPatient = new URLSearchParams(location.search).get('idPatient');
    const idDoctor = new URLSearchParams(location.search).get('idDoctor');
    const date = new URLSearchParams(location.search).get('date');

    useEffect(() => {
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
                console.log("Eroare: " + error);
                deleteCookie("Token");
                deleteCookie("Id");
                deleteCookie("Role");
                alert("Delogarea sa făcut cu succes!");
                navigate("/login");
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
        // pregatire date
        event.preventDefault();
        var obiectData = new Date(date);
        var an = obiectData.getFullYear();
        var luna = ('0' + (obiectData.getMonth() + 1)).slice(-2);
        var zi = ('0' + obiectData.getDate()).slice(-2);
        var ora = ('0' + obiectData.getHours()).slice(-2);
        var minute = ('0' + obiectData.getMinutes()).slice(-2);
        console.log(idPatient + " " + idDoctor + " " + date)
        var dateFormat = an + "-" + luna + "-" + zi + " " + ora + ":" + minute;

        let objToPost = state;
        objToPost.date = dateFormat;
        objToPost.id_patient = idPatient;
        objToPost.id_physician = idDoctor;
        setState(prevState => {
            const newState = { ...prevState };
            newState.date = dateFormat;
            newState.id_patient = idPatient;
            newState.id_physician = idDoctor;
            return newState;
        });
        console.log("New state: " + state)
        // insert
        let token = getCookie("Token");
        let response = await fetch(`http://localhost:8086/api/pos/consultation`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token
            },
            body: JSON.stringify(state),
        });

        if (response.status === 401) {
            SetError("Acces neautorizat!");
            throw new Error("Acces neautorizat!");
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
        if (response.status !== 201) {
            SetError("Eroare la server sau date invalide!");
            throw new Error("Eroare la server!");
        }

        console.log("Status 201");

        let data = await response.json();
        console.log('Response from server:', data);
        SetError("");
        alert("Consultație adăugată cu succes!")
    }

    const handleDiagnosticChange = (e) => {
        let olsState = state;
        state.diagnostic = e.target.value;
        SetNewInvestigation(olsState);
        console.log(e.target.value)
    };

    const handleNumeChange = (e) => {
        let oldInvestigation = newInvestigation;
        oldInvestigation.name = e.target.value;
        SetNewInvestigation(oldInvestigation);
    };

    const handleDaysChange = (e) => {
        let oldInvestigation = newInvestigation;
        oldInvestigation.days = e.target.value;
        SetNewInvestigation(oldInvestigation);
    };


    const handleResltChange = (e) => {
        let oldInvestigation = newInvestigation;
        oldInvestigation.result = e.target.value;
        SetNewInvestigation(oldInvestigation);
    };

    const handleSubmitInvestigatios = (e) => {
        e.preventDefault();
        setState(prevState => {
            const newState = { ...prevState };
            newState.investigations.push({
                id: null,
                name: newInvestigation.name,
                days: newInvestigation.days,
                result: newInvestigation.result
            });

            return newState;
        });

        alert("A fost creată investigatia!");
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
            <h2>
                Creați consultația pentru programarea selectată
            </h2>
            <div id="personalData">
                <form onSubmit={handleSubmit}>
                    <h2>Consultație:</h2>
                    <label>Diagnostic:</label>
                    <input
                        type="text"
                        onChange={handleDiagnosticChange}
                    />
                    <h2>Lista de investigații:</h2>
                    {state.investigations.map((investigation, index) => (
                        <p id="listInvestigations">
                            {`Investigație ${index + 1}: ${investigation.name}, ${investigation.days} zile, Rezultat: ${investigation.result}`}
                            <br />
                        </p>
                    ))}
                    <p>{error}</p>
                    <input type="submit" value="Creare" />
                </form>
                <form onSubmit={handleSubmitInvestigatios}>
                    <h2>Investigație pentru consultație:</h2>
                    <label>Nume:</label>
                    <input
                        type="text"
                        onChange={handleNumeChange}
                    />
                    <br></br>
                    <label>Zile:</label>
                    <input
                        type="number"
                        onChange={handleDaysChange}
                    />
                    <br></br>
                    <label>Rezultat:</label>
                    <input
                        type="text"
                        onChange={handleResltChange}
                    />
                    <br></br>
                    <input type="submit" value="Creare" />
                </form>
            </div>
        </div>
    );
};


export default CreateConsutation;
