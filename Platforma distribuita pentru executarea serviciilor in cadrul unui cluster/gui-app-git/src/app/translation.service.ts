import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CookiesService } from './cookies.service';


@Injectable({
  providedIn: 'root',
})

export class TranslationService {

  constructor(private http: HttpClient, private cookies: CookiesService) { }

  getTranslations(): any {
    let language: string | null = this.cookies.getByName("language");
    if (language == null || language == undefined || language.length == 0) {
      language = "ro";
    }

    let translations: any = {
      "en": {
        "home": {
          "message": "Welcome to our distributed platform, designed to manage and execute services in a dynamic cluster. Our primary mission is to improve efficiency, ensure scalability, and enhance security in the realm of distributed applications. Our platform offers a comprehensive approach that covers the publishing and execution of services.",
          "title": "Distributed platform for service execution within a cluster",
          "message2": "Join us today and unlock the unlimited potential of distributed applications!"
        },

        "navbar": {
          "home": "Home",
          "avaiable": "Available services",
          "run": "Run your code",
          "settings": "Settings",
          "results": "Results",
          "contact": "Contact",
          "about": "About",
          "logout": "Logout"
        },
        "run": {
          "title": "Upload Code for Execution",
          "message": "Our platform offers you an exceptional dynamic experience! You can upload and execute code in various formats: .class files, .java files, zip archives containing any of the aforementioned files, and jar files. To bring your code to life, simply select the type of files you wish to upload (the files must be of a single type), upload the files, and choose the startup class name from the dropdown menu. This class must contain a non-static method called \"run.\" Enjoy the magic of dynamic programming!",
          "type": "Select type:",
          "fileSelect": "Select the desired files:",
          "select": "Select",
          "fileSelected": "The selected files are: ",
          "startFile": "Main Class",
          "run": "Run",
          "external": "External Libraries",
          "example": "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.4/commons-lang3-3.4.jar org.apache.commons:commons-lang3:3.4 .",
          "messageExernalLibrary": "External libraries must be specified using the following fields: groupId, artifactId, version, which can be obtained from:",
          "executeNavbar": "Upload Code",
          "dependencies": "Dependencies",
          "files": "Files",
          "saveCode": "Publish your code to make it available to others!",
          "console": "Console",
          "publish": "Publish",
          "appName": "Application Name",
          "description": "Application Description",
          "invalidName": "The application name must be at least five characters long.",
          "send": "Submit",
          "by": "By",
          "info": "Code Information",
          "filesAvaiable": "Available Files:",
          "avaiable": "The code is public:",
          "typeRun": "Execution Type",
          "method1": "Method 1",
          "method2": "Method 2",
          "text1": "Method 1: Requires appending a string resulting from calling the getPath() method available in this file to the path. The code will execute faster. The startup method will be named run() and will be non-static.",
          "text2": "Method 2: The executed code does not require additional processing but runs slower."
        },
        "startUp": {
          "message": "Log in or create an account to have the opportunity to run code and explore our numerous services! You can learn more by accessing this link: ",
          "link": "information",
          "welcome": "Welcome!",
          "login": "Login",
          "or": "Or",
          "create": "Create Account",
          "language1": "Romanian",
          "language2": "English"
        },
        "createAccount": {
          "title": "Create Account",
          "username": "Username",
          "password": "Password",
          "password2": "Repeat Password",
          "email": "Email",
          "firstName": "First Name",
          "lastName": "Last Name",
          "description": "Description",
          "submit": "Create",
          "message": "Already have an account? Log in!",
          "passwordMessage": "The passwords do not match!",
          "succes": "User successfully created! You now need to log in to the newly created account!"
        },
        "login": {
          "title": "Login",
          "username": "Username or Email",
          "password": "Password",
          "message": "Don't have an account? Sign up!",
          "submit": "Log in",
          "expirate": "Session expired, please log in again!",
          "passwordReset": "Forgot your password?"
        },
        "popup": {
          "close": "Close"
        },
        "about": {
          "title": "Distributed platform for cluster service execution",
          "aboutText": "The distributed platform for service execution within a cluster, created by Mădălina-Elena Boacă between 2023-2024, offers a space where programmers can publish software solutions accessible to users from various fields. Through it, users can access and utilize services without requiring advanced IT knowledge. This simplifies the process of accessing technology and benefits both programmers and users.",
          "button": "User Guide"
        },
        "save": {
          "saveSuccess": "The code has been successfully published!"
        },
        "delete": {
          "deleteSucces": "The published program has been successfully deleted!"
        },
        "logs": {
          "type": "Choose the type of node for which you want logs",
          "time": "Date - a substring of the date in the format DD/MM/YYYY hh:mm",
          "address": "Node IP address",
          "typeErr": "Error type (INFO, WARNING, ERROR, SUCCESS, MESSAGE)",
          "userId": "User ID",
          "run": "Extract logs",
          "messages": "Extracted logs",
          "timeT": "Time",
          "addressT": "IP Address",
          "userIdT": "User ID",
          "typeErrT": "Log type",
          "messageT": "Message"
        },
        "checkCode": {
          "message": "To complete account creation, enter the code received at the email address ",
          "finish": "Submit"
        },
        "resetpassword": {
          "messageEmailTitle": "Password Reset",
          "messageEmail": "Email address:",
          "finish": "Submit"
        },
        "codeResetPassword": {
          "messageEmailTitle": "Code received via email for password reset",
          "finish": "Submit"
        },
        "changePassword": {
          "title": "Password Reset",
          "password1": "New password:",
          "password2": "Repeat password:",
          "send": "Change",
          "errorNotEqual": "The two passwords do not match",
          "succes": "Password successfully changed! Please log in with the new password!"
        },
        "contact": {
          "title": "Contact form",
          "name": "Name",
          "email": "Email",
          "subject": "Subject",
          "content": "Content",
          "send": "Submit",
          "succes": "Your feedback has been successfully sent!"
        },
        "feedback": {
          "title": "User Feedback",
          "email": "Email address",
          "subject": "Subject",
          "content": "Content",
          "delete": "Feedback has been successfully deleted!",
          "action": "Action",
          "deleteButton": "Delete"
        },
        "settings": {
          "language": "Display language",
          "profile": "Profile settings",
          "note": "In the following form, you can modify your first name, last name, username, and description. The email address cannot be changed.",
          "firstname": "First Name",
          "lastname": "Last Name",
          "username": "Username",
          "description": "Description",
          "password": "To change your password, please click here.",
          "button": "Update",
          "changePassword": "Change password",
          "changePasswordMessage1": "To change your password, you can access the following link ",
          "link": " change password",
          "changePasswordMessage2": " after accessing it, you will receive a code by email that you need to enter on the page that will open.",
          "sucees": "The update request has been successfully made!"
        },
        "info": {
          "title": "Usage instructions",
          "user": {
            "title": "User",
            "info1": "On this platform, a user can have only one account assigned to an email address.",
            "info2": "Account creation requires confirmation by verifying a code sent via email.",
            "info3": "After creation, only the username, last name, first name, description, and password can be modified.",
            "info4": "To reset the password, both when the user is logged in and when they have forgotten it, a code sent via email is required.",
            "info5": "After account creation, it cannot be deleted!"
          },
          "code": {
            "title": "Code execution",
            "info1": "Our platform allows Java code execution in two different methods. The first method allows for much faster execution, but the user must use a library for file handling, and the main method must be transformed into a non-static method called run. In the second method, the submitted code does not require special modifications, but the response time is longer.",
            "info2": "The code uploaded to the platform can be in .java, .class, or .jar files. If the code uses dependencies that are not included by default in the JVM, the resulting jar will include them from the user.",
            "info3": "Support is provided for terminal display, keyboard input, and file handling.",
            "info4": "Support is not provided for network operations. Additionally, system-level calls or attempts to access files that do not belong to the user (in the first execution method) are not allowed. All attempts will be rejected by throwing exceptions.",
            "info5": "Only one execution session can be open at a time on an account.",
            "info6": "Execution time is limited to twenty minutes.",
            "info7": "Multithreading operations are allowed, but thread groups cannot be created."
          },
          "startUp": "Return to the main page"
        },
        "errorTime": "The persistent connection has expired or was closed due to an error!",
        "errorWebSocket": "Connection to the execution service failed! Please try again!"


      },
      "ro": {
        "home": {
          "message": "Bine ați venit pe platforma noastră distribuită,  proiectată pentru a gestiona și executa servicii într-un  cluster dinamic. Misiunea noastră principală este să îmbunătățim eficiența, să asigurăm scalabilitatea și să consolidăm securitatea în domensiul aplicațiilor distribuite. Platforma noastră oferă o abordare cuprinzătoare care acoperă publicarea și executarea serviciilor.",
          "title": "Platforma distribuită pentru executarea serviciilor în cadrul unui cluster",
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
          "logout": "Ieșire"
        },
        "run": {
          "title": "Încărcare cod pentru execuție",
          "message": "Platforma noastră vă oferă o experiență dinamică de excepție! Puteți încărca și executa cod în moduri variate: fișiere .class, fișiere .java, arhive zip cu toate fișierele menționate anterior și fișiere jar. Pentru a da viață codului dvs., trebuie doar să selectați tipul de fișiere pe care doriți să le încărcați (fișierele trebuie să fie dintr-un singur tip), să încărcați fișierele și să alegeți din meniul derulant numele clasei de start-up. Aceasta trebuie să conțină o metodă nestatică numită \"run\". Bucurați-vă de magia programării dinamice!",
          "type": "Selectează tipul:",
          "fileSelect": "Selectați fișierele dorite:",
          "select": "Selectează",
          "fileSelected": "Fișierele selectate sunt: ",
          "startFile": "Clasa principală",
          "run": "Execută",
          "external": "Librării externe",
          "example": "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.4/commons-lang3-3.4.jar org.apache.commons:commons-lang3:3.4 .",
          "messageExernalLibrary": "Librăriile externe trebuie specificate prin următoarele câmpuri: groupId, artifactId, version care pot fi obținute din:",
          "executeNavbar": "Încărcare cod",
          "dependencies": "Dependențe",
          "files": "Fișiere",
          "saveCode": "Publică codul tău pentru a fi disponibil și altora!",
          "console": "Consolă",
          "publish": "Publică",
          "appName": "Numele aplicației",
          "description": "Descrierea aplicației",
          "invalidName": "Numele aplicației trebuie să aibă minim cinci caractere.",
          "send": "Trimite",
          "by": "De",
          "info": "Informații despre cod",
          "filesAvaiable": "Fișierele disponibile:",
          "avaiable": "Codul este public:",
          "typeRun": "Tipul de execuție",
          "method1": "Metoda 1",
          "method2": "Metoda 2",
          "text1": "Metoda 1: Necesită conctatenarea la path a unui string ce rezultă din apelul metodei getPath() disponibilă în acest fișier. Codul se va executa mai rapid. Metoda de start-up va fi denumită run() și va fi nestatică.",
          "text2": "Metoda 2: Codul executat nu are nevoie de procesări suplimentare, dar se execută mai greu."
        },
        "startUp": {
          "message": "Conectează-te sau creează un cont pentru a avea ocazia de a executa cod și de a explora numeroasele noastre servicii! Puteți afla mai mai multe accesând acest link: ",
          "link": "informații",
          "welcome": "Bine ai venit!",
          "login": "Conectare",
          "or": "Sau",
          "create": "Creare cont",
          "language1": "Română",
          "language2": "Engleză"
        },
        "createAccount": {
          "title": "Crează cont",
          "username": "Nume de utilizator",
          "password": "Parolă",
          "password2": "Repetare parolă",
          "email": "Email",
          "firstName": "Prenume",
          "lastName": "Nume",
          "description": "Descriere",
          "submit": "Creare",
          "message": "Ai deja cont? Conectează-te!",
          "passwordMessage": "Cele două parole nu corespund!",
          "succes": "Utilizatorul a fost creat cu succes! Acum trebuie să te conectezi la contul tocmai creat!"
        },
        "login": {
          "title": "Conectare",
          "username": "Nume de utilizator sau email",
          "password": "Parolă",
          "message": "Nu ai cont? Înregistrează-te!",
          "submit": "Conectează-te",
          "expirate": "Sesiunea a expirat vă rugăm să vă reconectați!",
          "passwordReset": "Ai uitat parola?"
        },
        "popup": {
          "close": "Închide"
        },
        "about": {
          "title": "Platforma distribuită pentru execuția serviciilor în cadrul unui cluster",
          "aboutText": "Platforma distribuită pentru execuția serviciilor în  cadrul unui cluster, creată de Boacă Mădălina-Elena între 2023-2024, oferă un spațiu unde programatorii pot publica soluții software accesibile utilizatorilor din diverse domenii. Prin intermediul ei, utilizatorii pot accesa și utiliza servicii fără a fi necesare cunoștințe avansate de IT. Aceasta simplifică procesul de acces la tehnologie și aduce beneficii atât programatorilor, cât și utilizatorilor.",
          "button": "Ghid de utilizare"
        },
        "save": {
          "saveSuccess": "Codul a fost publicat cu succes!"
        },
        "delete": {
          "deleteSucces": "Programul publicat a fost șters cu success!"
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
          "messageT": "Mesajul"
        },
        "checkCode": {
          "message": "Pentru a finaliza crearea contului introduceți codul primit la adresa de email ",
          "finish": "Trimite"
        },
        "resetpassword": {
          "messageEmailTitle": "Resetare parolă",
          "messageEmail": "Adresa de email:",
          "finish": "Trimite"
        },
        "codeResetPassword": {
          "messageEmailTitle": "Codul primit pe email pentru resetarea parolei",
          "finish": "Trimite"
        },
        "changePassword": {
          "title": "Resetare parolă",
          "password1": "Noua parolă:",
          "password2": "Repetă parola:",
          "send": "Schimbă",
          "errorNotEqual": "Cele două parole nu corespund",
          "succes": "Parola a fost schimbată cu succes! Acum vă rugăm să vă conectați cu ea!"
        },
        "contact": {
          "title": "Formular de contact",
          "name": "Nume",
          "email": "Email",
          "subject": "Subiect",
          "content": "Conținut",
          "send": "Trimite",
          "succes": "Feedback-ul dumneavoastră a fost trimis cu succes!"
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
        "settings": {
          "language": "Limba de afișare",
          "profile": "Setări pentru profil",
          "note": "În următorul formular puteți modifica numele, prenumele, numele de utilizator și descrierea. Adresa de email nu poate fi modificată.",
          "firstname": "Nume",
          "lastname": "Prenume",
          "username": "Nume de utilizator",
          "desciption": "Descriere",
          "password": "Pentru schimbarea parolei vă rugăm să apăsați aici.",
          "button": "Modificare",
          "changePassword": "Shimbarea parolei",
          "changePasswordMessage1": "Pentru a schimba parola puteți accesa link-ul următor ",
          "link": " schimbarea parolei",
          "changePasswordMessage2": " în urma accesări ve-ți primi un cod pe email pe care trebuie să îl introduceți în pagina care se va deschide.",
          "sucees": "Cererea de modificare a fost făcută cu succes!"
        },
        "info": {
          "title": "Instrucțiuni de utilizare",
          "user": {
            "title": "Utilizator",
            "info1": "În cadrul acestei platforme, un utilizator poate avea un singur cont asignat unei adrese de email.",
            "info2": "Pentru crearea contului este necesară o confirmare care constă în verificarea unui cod trimis prin email.",
            "info3": "După creare, se pot modifica doar numele de utilizator, numele de familie, prenumele, descrierea și parola.",
            "info4": "Pentru resetarea parolei, atât atunci când utilizatorul este conectat, cât și atunci când a uitat-o, este necesar un cod trimis prin email.",
            "info5": "După crearea contului, acesta nu poate fi șters!"
          },
          "code": {
            "title": "Executare cod",
            "info1": "Platforma noastră permite executarea de cod Java în două metode diferite. Prima presupune o execuție mult mai rapidă, dar utilizatorul trebuie să utilizeze o bibliotecă pentru lucrul cu fișiere, iar metoda main trebuie transformată într-o metodă nestatică numită run. În cea de-a doua metodă, codul transmis nu necesită modificări speciale, dar timpul de răspuns este mai îndelungat.",
            "info2": "Codul încărcat pe platformă poate fi în fișiere .java, .class sau .jar. Dacă codul folosește dependențe care nu sunt incluse implicit în JVM, jar-ul rezultat le va include de la utilizator.",
            "info3": "Se oferă suport pentru afișarea în terminal, citirea de la tastatură și lucrul cu fișiere.",
            "info4": "Nu se oferă suport pentru operații în rețea. De asemenea, nu se pot emite apeluri la nivelul sistemului de operare sau încerca să se acceseze fișiere care nu aparțin utilizatorului (în cadrul primei metode de execuție). Toate încercările vor fi respinse prin aruncarea de excepții.",
            "info5": "Pe un cont poate fi deschisă doar o sesiune de execuție la un moment dat.",
            "info6": "Timpul de execuție este limitat la douăzeci de minute.",
            "info7": "Se permit operații multithreading, dar nu se pot crea grupuri de thread-uri."
          },
          "startUp": "Revenire la pagina principală",
          "error": "A apărut o eroare necunoscută:",
          "errorTime": "Conexiunea persistentă a expirat sau a fost închisă din cauza unei erori! ",
          "errorWebSocket": "Conectarea la serviciul de execuție a eșuat! Vă rugăm să reîncercați!"
        }
      }
    };
    return translations[language];
  }
}
