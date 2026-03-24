import { Injectable } from '@angular/core';
import { CookiesService } from './cookies.service';

@Injectable({
  providedIn: 'root'
})
export class TranslationService {
  constructor(private cookies: CookiesService) { }

  getTranslations(): any {
    let language: string | null = this.cookies.getByName("language");
    if (language == null || language == undefined || language.length == 0) {
      language = "ro";
    }

    let translations: any = {
      "en": {
        "home": {
          "message": "Welcome to our innovative and distributed platform, meticulously designed to manage and execute services in a dynamic cluster environment. Our primary mission is to improve efficiency, ensure scalability, and enhance security in the contemporary landscape of distributed applications. Our platform offers a comprehensive approach covering service deployment, management, and continuous monitoring.",
          "title": "Distributed Platform for Service Execution in Cluster",
          "message2": "Join us today and unlock the unlimited potential of distributed applications!"
        },
        "navbar": {
          "home": "Home",
          "avaiable": "Available Services",
          "run": "Run Your Code",
          "settings": "Settings",
          "results": "Results",
          "contact": "Contact",
          "about": "About",
          "logout": "Logout",
          "logs": "Logs",
          "feedback": "Feedback"
        },
        "login": {
          "title": "Login",
          "username": "Username or email",
          "password": "Password",
          "message": "Don't have an account? Register!",
          "submit": "Login",
          "expirate": "Session expired, please log in again!",
          "message403": "This account does not have the necessary permissions to perform this operation!",
          "passwordReset": "Forgot password?"
        },
        "popup": {
          "close": "Close"
        },
        "logs": {
          "type": "Choose the node type for which you want the logs",
          "time": "Date - a substring of the date in the format DD/MM/YYYY hh:mm",
          "address": "Node IP address",
          "typeErr": "Error type (INFO, WARNING, ERROR, SUCCESS, MESSAGE)",
          "userId": "User ID",
          "run": "Extract logs",
          "messages": "Extracted logs",
          "timeT": "Time",
          "addressT": "IP Address",
          "userIdT": "User ID",
          "typeErrT": "Log Type",
          "messageT": "Message"
        },
        "feedback": {
          "title": "User Feedback",
          "email": "Email Address",
          "subject": "Subject",
          "content": "Content",
          "delete": "Feedback has been successfully deleted!",
          "action": "Action",
          "deleteButton": "Delete"
        },
        "startUp": {
          "message": "Conectați-vă sau creați un cont pentru a avea ocazia de a executa cod și de a explora numeroasele noastre servicii! Puteți afla mai mai multe accesând acest link: ",
          "link": "informații",
          "welcome": "Bine ai venit!",
          "login": "Conectare",
          "or": "Sau",
          "create": "Creare cont",
          "language1": "Română",
          "language2": "Engleză"
        },
      },
      "ro": {
        "home": {
          "message": "Bine ați venit pe platforma noastră inovatoare și distribuită, meticulos proiectată pentru a gestiona și executa servicii într-un mediu cluster dinamic. Misiunea noastră principală este să îmbunătățim eficiența, să asigurăm scalabilitatea și să consolidăm securitatea în peisajul contemporan al aplicațiilor distribuite. Platforma noastră oferă o abordare cuprinzătoare care acoperă implementarea serviciilor, gestionarea acestora și monitorizarea continuă.",
          "title": "Platforma Distribuită pentru Execuția Serviciilor în Cluster",
          "message2": "Alăturați-vă nouă astăzi și deblocați potențialul nelimitat al aplicațiilor distribuite!"
        },
        "navbar": {
          "home": "Acasă",
          "avaiable": "Servicii disponibile",
          "run": "Execută codul tău",
          "settings": "Setări",
          "results": "Rezultate",
          "contact": "Contact",
          "about": "Despre",
          "logout": "Ieșire",
          "logs": "Log-uri",
          "feedback": "Feedback"
        },
        "login": {
          "title": "Conectare",
          "username": "Nume de utilizator sau email",
          "password": "Parolă",
          "message": "Nu ai cont? Înregistrează-te!",
          "submit": "Conectează-te",
          "expirate": "Sesiunea a expirat vă rugăm să vă reconectați!",
          "message403": "Acest cont nu are permisiunile necesare pentru a face această operație!",
          "passwordReset": "Ai uitat parola?"
        },
        "popup": {
          "close": "Închide"
        },
        "logs": {
          "type": "Alege tipul de nod pentru care doriți log-urile",
          "time": "Data - un substring din dată în formatul DD/MM/YYYY hh:mm",
          "address": "Adresa ip a nodului",
          "typeErr": "Tipul erori (INFO, WARNING, ERROR, SUCCESS, MESSAGE)",
          "userId": "Id-ul utilizatorului",
          "run": "Extrage log-urile",
          "messages": "Log-urile extrase",
          "timeT": "Timpul",
          "addressT": "Adresa IP",
          "userIdT": "Id-ul utilizatorului",
          "typeErrT": "Tipul log-ului",
          "messageT": "Mesajul",
          "microservice": "Microserviciul"
        },
        "feedback": {
          "title": "Feedback utilizatori",
          "email": "Adresa de email",
          "subject": "Subiect",
          "content": "Conținutul",
          "delete": "Feedback-ul a fost șters cu succes!",
          "action": "Acțiune",
          "deleteButton": "Ștergere"
        },
        "startUp": {
          "message": "Conectați-vă sau creați un cont pentru a avea ocazia de a executa cod și de a explora numeroasele noastre servicii! Puteți afla mai mai multe accesând acest link: ",
          "link": "informații",
          "welcome": "Bine ai venit!",
          "login": "Conectare",
          "or": "Sau",
          "create": "Creare cont",
          "language1": "Română",
          "language2": "Engleză"
        },
      }
    };
    return translations[language];
  }
}
