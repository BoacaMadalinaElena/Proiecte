#!/bin/bash

# Script pentru consultatie
consultatie_script="consultatie.sh"
if [ -f "$consultatie_script" ]; then
    gnome-terminal --tab --title="Consultatie" -- bash -c "./$consultatie_script; read -p 'Press Enter to close this terminal.'"
else
    echo "Scriptul pentru Consultatie nu exista."
fi

# Script pentru doctor
doctor_script="doctor.sh"
if [ -f "$doctor_script" ]; then
    gnome-terminal --tab --title="Doctor" -- bash -c "./$doctor_script; read -p 'Press Enter to close this terminal.'"
else
    echo "Scriptul pentru Doctor nu exista."
fi

# Script pentru pacient
pacient_script="pacient.sh"
if [ -f "$pacient_script" ]; then
    gnome-terminal --tab --title="Pacient" -- bash -c "./$pacient_script; read -p 'Press Enter to close this terminal.'"
else
    echo "Scriptul pentru Pacient nu exista."
fi

# Script pentru programare
programare_script="programare.sh"
if [ -f "$programare_script" ]; then
    gnome-terminal --tab --title="Programare" -- bash -c "./$programare_script; read -p 'Press Enter to close this terminal.'"
else
    echo "Scriptul pentru Programare nu exista."
fi

# Script pentru idm
programare_script="idm.sh"
if [ -f "$programare_script" ]; then
    gnome-terminal --tab --title="IDM" -- bash -c "./$programare_script; read -p 'Press Enter to close this terminal.'"
else
    echo "Scriptul pentru IDM nu exista."
fi

# Script pentru gateway
gateway_script="gateway.sh"
if [ -f "$gateway_script" ]; then
    gnome-terminal --tab --title="gateway" -- bash -c "./$gateway_script; read -p 'Press Enter to close this terminal.'"
else
    echo "Scriptul pentru gateway nu exista."
fi

# Script pentru gRPC-client
idm_client_script="idm-client.sh"
if [ -f "$idm_client_script" ]; then
    gnome-terminal --tab --title="IDM - client" -- bash -c "./$idm_client_script; read -p 'Press Enter to close this terminal.'"
else
    echo "Scriptul pentru IDM client nu exista."
fi

# Script pentru gRPC-client-rest
idm_client_rest="idm_client_rest.sh"
if [ -f "$idm_client_rest" ]; then
    gnome-terminal --tab --title="IDM - client - rest" -- bash -c "./$idm_client_rest; read -p 'Press Enter to close this terminal.'"
else
    echo "Scriptul pentru IDM client rest nu exista."
fi

# Script pentru idm
idm_client_script="idm_client.sh"
if [ -f "$idm_client_script" ]; then
    gnome-terminal --tab --title="IDM rest" -- bash -c "./$idm_client_script; read -p 'Press Enter to close this terminal.'"
else
    echo "Scriptul pentru IDM nu exista."
fi
