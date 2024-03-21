import React, { useEffect ,useState} from "react";
import "./HomeDoctor.css"
import { useNavigate } from 'react-router-dom';

const HomeDoctor = () => {
    const navigate = useNavigate();
    const [specialization, setSpecialization] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setlastName] = useState('');
    const [email, setEmail] = useState('');
    const [telephone, setTelephone] = useState('');

    useEffect(() => {
        let token = getCookie("Token");

        fetch('http://localhost:8086/api/pos/physicians/id_user/' + getCookie("Id"), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                Authorization: token
            },
        })
            .then((response) => response.json())
            .then((data) => {
                setSpecialization(data.specialization);
                setFirstName(data.last_name);
                setlastName(data.first_name);
                setEmail(data.email);
                setTelephone(data.telephone);
            })
            .catch((error) => {
                console.log(error);
            });
    }, []);

    const handleClick = () => {
        let token = getCookie("Token");
        console.log("Token-ul este: " + token);
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

    return (
        <div>
            <div id="navbar">
                <ul>
                    <li><a href="/homeDoctor">Home doctor</a></li>
                    <li><a href="/patientsDoctor">Pacienți dumneavoastră</a></li>
                </ul>
                <button onClick={handleClick}>Logout</button>
            </div>
            <h1>Bine ai venit, domnule doctor!</h1>
            <br></br>
            <div id="personalData">
                <h2>În cadrul site-ului nostru dumneavoastră aveți următoarele date:</h2>
                <p class="homePacient">Numele: {lastName}</p>
                <p class="homePacient">Prenumele: {firstName}</p>
                <p class="homePacient">Email-ul: {email}</p>
                <p class="homePacient">Numărul de telefon: {telephone}</p>
                <p class="homePacient">Specializare: {specialization}</p>
            </div>
        </div>
    );

   
}
export default HomeDoctor;