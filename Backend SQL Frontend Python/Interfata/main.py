import tkinter as tk
import traceback
from tkinter import font
from PIL import ImageTk, Image
import cx_Oracle
from tkinter import ttk


def afisareDetaliiInitiale(e=None):
    nume = app.details.afectiuneC.get()
    app.cursor.execute('select id from afectiune where nume = +\'' + nume + '\'')
    rows = app.cursor.fetchall()
    try:
        app.cursor.execute('select * from detalii_afectiune where afectiune_id = ' + str(rows[0][0]))
        rows = app.cursor.fetchall()

        app.details.gravitateT.delete('1.0', tk.END)
        app.details.gravitateT.insert(tk.END, rows[0][0])

        app.details.vindecareT.delete('1.0', tk.END)
        app.details.vindecareT.insert(tk.END, rows[0][1])

        app.details.tratamentC.set(rows[0][2])

        app.details.suicidT.delete('1.0', tk.END)
        app.details.suicidT.insert(tk.END, rows[0][3])

        app.details.specificitateC.set(rows[0][4])
        app.details.asistentaPersonalaC.set(rows[0][5])

    except Exception as e:
        print(e)
        traceback.print_exc()


def seteazaValoriActualePacient(e=None):
    nume = app.patient.pacientC.get()

    app.cursor.execute('ALTER SESSION SET NLS_DATE_FORMAT = \'DD-MM-YYYY\'')
    for i in app.patient.pacienti:
        if nume.__contains__(str(i)):
            nr = i
            try:
                app.cursor.execute('select * from pacient where numar_telefon = ' + nr)
                date = app.cursor.fetchall()
                app.patient.numeT.delete("1.0", "end")
                app.patient.numeT.insert(tk.END, date[0][1])

                app.patient.prenumeT.delete("1.0", "end")
                app.patient.prenumeT.insert(tk.END, date[0][2])

                app.patient.dataNasteriiT.delete("1.0", "end")
                app.patient.dataNasteriiT.insert(tk.END, date[0][3])

                app.patient.telefonT.delete("1.0", "end")
                app.patient.telefonT.insert(tk.END, date[0][4])

                app.patient.genC.set(date[0][5])

                app.patient.adresaT.delete("1.0", "end")
                app.patient.adresaT.insert(tk.END, date[0][6])
            except IndexError:
                open_popupEror('Selectati un element de modificat!\nCaseta vida!')
            except cx_Oracle.DatabaseError:
                open_popupEror('Selectati un element de modificat!\nCaseta vida!')


def modificaPacient(e=None):
    nume = app.patient.pacientC.get()
    app.patient.pacientC.set('')

    for i in app.patient.pacienti:
        if nume.__contains__(str(i)):
            nr = i
            try:
                app.patient.inserareNume = app.patient.numeT.get("1.0", "end")
                app.patient.inserarePrenume = app.patient.prenumeT.get("1.0", "end")
                app.patient.inserareDataNastere = app.patient.dataNasteriiT.get("1.0", "end")
                app.patient.inserareTelefon = app.patient.telefonT.get("1.0", "end")
                app.patient.inserareGen = app.patient.genC.get()
                app.patient.inserareAdresa = app.patient.adresaT.get("1.0", "end")

                app.patient.inserareNume = app.patient.inserareNume.replace('\n', '')
                app.patient.inserarePrenume = app.patient.inserarePrenume.replace('\n', '')
                app.patient.inserareDataNastere = app.patient.inserareDataNastere.replace(' ', '').replace('\n', '')[0:10]
                app.patient.inserareTelefon = app.patient.inserareTelefon.replace(' ', '').replace('\n', '')
                app.patient.inserareGen = app.patient.inserareGen.replace(' ', '').replace('\n', '')
                app.patient.inserareAdresa = app.patient.inserareAdresa.replace('\n', '')

                app.cursor.execute('ALTER SESSION SET NLS_DATE_FORMAT = \'YYYY-MM-DD\'')
                app.cursor.execute(
                    'update pacient set nume = \'' + app.patient.inserareNume + '\' where numar_telefon = ' + nr)
                app.cursor.execute('update pacient set prenume = \'' + app.patient.inserarePrenume + '\' where '
                                                                                                     'numar_telefon = '
                                                                                                     '' + nr)
                app.cursor.execute(
                    'update pacient set data_nasterii = \'' + app.patient.inserareDataNastere + '\' where '
                                                                                                'numar_telefon = ' + nr)
                app.cursor.execute(
                    'update pacient set numar_telefon = \'' + app.patient.inserareTelefon + '\' where numar_telefon = ' + nr)
                app.cursor.execute(
                    'update pacient set gen = \'' + app.patient.inserareGen + '\' where numar_telefon = ' + nr)
                app.cursor.execute(
                    'update pacient set adresa = \'' + app.patient.inserareAdresa + '\' where numar_telefon = ' + nr)

                app.cursor.execute('COMMIT')
                app.patient.numeT.delete("0.0", "end")
                app.patient.prenumeT.delete("0.0", "end")
                app.patient.dataNasteriiT.delete("0.0", "end")
                app.patient.telefonT.delete("0.0", "end")
                app.patient.adresaT.delete("0.0", "end")
                open_popup('Datele au fost modificate cu succes!')
                valori1 = []
                app.patient.pacienti = []
                app.cursor.execute('select nume,prenume,numar_telefon from pacient order by id asc')
                rows = app.cursor.fetchall()
                app.patient.pacienti = []
                for i in rows:
                    app.patient.pacienti.append(i[2])
                    valori1.append(str(i[0]) + ' ' + str(i[1]) + ' ' + str(i[2]))

                app.patient.pacientC['values'] = valori1  # se vor citi din baza de date
            except IndexError:
                open_popupEror('Selectati un element de sters!\nCaseta vida!')
                traceback.print_exc()
            except Exception as e:
                open_popupEror('Exista campuri necompletate sau cu valori eronate!')
                traceback.print_exc()


def selectAtributDeModificatFisa(e=None):
    # rows = ['Pacient','Medic psihiatru','Data eliberare fisa','Data revenire control','Medicamente','Afectiuni',
    # 'Cantitate','Mod de administrare']
    if app.sheet.modificareAtributFisa.get() == 'Pacient':
        app.HideAll()
        app.sheet.ShowSheet(5)
    if app.sheet.modificareAtributFisa.get() == 'Medic psihiatru':
        app.HideAll()
        app.sheet.ShowSheet(6)
    if app.sheet.modificareAtributFisa.get() == 'Data eliberare fisa':
        app.HideAll()
        app.sheet.ShowSheet(7)
    if app.sheet.modificareAtributFisa.get() == 'Data revenire control':
        app.HideAll()
        app.sheet.ShowSheet(8)
    if app.sheet.modificareAtributFisa.get() == 'Medicamente':
        app.HideAll()
        app.sheet.ShowSheet(9)
    if app.sheet.modificareAtributFisa.get() == 'Afectiuni':
        app.HideAll()
        app.sheet.ShowSheet(10)

def schimbareNumePacientFisa(e=None):
    app.sheet.intrarePacient = app.sheet.strPacient.get()
    app.sheet.idFisaDeModificat = app.sheet.modificareC.get()
    for i in app.sheet.pacienti:
        if app.sheet.intrarePacient.__contains__(str(i)):
            app.sheet.intrareIDPacient = i
    app.cursor.execute('select id from pacient where numar_telefon = ' + str(app.sheet.intrareIDPacient))
    idpacient = app.cursor.fetchall()
    app.cursor.execute(
        'update FISA_MEDICALA set PACIENT_ID = ' + str(idpacient[0][0]) + ' where id = ' + app.sheet.idFisaDeModificat)
    app.cursor.execute('COMMIT')
    open_popup("Datele au fost modificate!")


def schimbareNumePsihiatruFisa(e=None):
    app.sheet.intrarePsihiatru = app.sheet.strMedic.get()
    app.sheet.idFisaDeModificat = app.sheet.modificareC.get()
    for i in app.sheet.psihiatri:
        if app.sheet.intrarePsihiatru.__contains__(str(i[1])):
            app.sheet.intrareIDMedic = i[0]
    app.cursor.execute('update FISA_MEDICALA set PSIHIATRU_ID = ' + str(
        app.sheet.intrareIDMedic) + ' where id = ' + app.sheet.idFisaDeModificat)
    app.cursor.execute('COMMIT')
    open_popup("Datele au fost modificate!")


def schimbaMedicamenteFisa(e=None):
    app.sheet.idFisaDeModificat = app.sheet.modificareC.get()
    app.sheet.intrareMedicamente = []
    try:
        for i in app.sheet.medicamenteLB.curselection():
            app.sheet.intrareMedicamente.append(i)
        cantitate = app.sheet.cantitateT.get("1.0", "end")
        cantitati = cantitate.split('\n')
        app.sheet.intrareOrar = app.sheet.detaliiT.get("1.0", "end")
        programe = app.sheet.intrareOrar.split('\n')
        cantitati.remove('')
        programe.remove('')
        listMed = []
        for i in app.sheet.intrareMedicamente:
            listMed.append(app.sheet.valuesMed[i][0])
        app.cursor.execute('delete from MDT_FISA_FK where FISA_MEDICALA_ID = ' + app.sheet.idFisaDeModificat)
        app.cursor.execute('commit')
        for i in range(len(listMed)):
            app.cursor.execute('insert into MDT_FISA_FK values(' + str(listMed[i]) + ', ' + str(
                app.sheet.idFisaDeModificat) + ',' + str(int(cantitati[i])) + ',\'' + programe[i] + '\')')
        app.cursor.execute('commit')
        open_popup("Datele au fost modificate!")
    except Exception as e:
        open_popupEror('Completati toate campurile cu valori valide!')
        traceback.print_exc()


def schimbaAfectiuniFisa(e=None):
    app.sheet.idFisaDeModificat = app.sheet.modificareC.get()
    app.sheet.intrareAfectiuni = []
    for i in app.sheet.afectiuniLB.curselection():
        app.sheet.intrareAfectiuni.append(i)
    listAfectiuni = []
    for i in app.sheet.intrareAfectiuni:
        listAfectiuni.append(app.sheet.valuesAf[i][0])
    app.cursor.execute('delete from fisa_afectiune_fk where FISA_MEDICALA_ID = ' + app.sheet.idFisaDeModificat)
    for i in range(len(listAfectiuni)):
        app.cursor.execute('insert into fisa_afectiune_fk values(' + str(app.sheet.idFisaDeModificat) + ',' + str(
            listAfectiuni[i]) + ')')
    app.cursor.execute('commit')
    open_popup("Datele au fost modificate!")


def schimbaDataEliberareFisa(e=None):
    app.sheet.intrareDataEliberare = app.sheet.modficareDataEliberareT.get("1.0", "end")
    app.sheet.idFisaDeModificat = app.sheet.modificareC.get()
    app.cursor.execute('ALTER SESSION SET NLS_DATE_FORMAT = \'DD-MM-YYYY\'')
    try:
        app.cursor.execute(
            'update FISA_MEDICALA set DATA_ELIBERARE = \'' + app.sheet.intrareDataEliberare + '\' where id = ' + app.sheet.idFisaDeModificat)
        app.cursor.execute('COMMIT')
        open_popup("Datele au fost modificate!")
    except cx_Oracle.DatabaseError as e:
        open_popupEror('Eroare! Data nu este valida!')
        traceback.print_exc()


def schimbaDataRevenireFisa(e=None):
    app.sheet.intrareDataRevenire = app.sheet.modficareDataRevenireT.get("1.0", "end")
    app.sheet.idFisaDeModificat = app.sheet.modificareC.get()
    app.cursor.execute('ALTER SESSION SET NLS_DATE_FORMAT = \'DD-MM-YYYY\'')
    try:
        app.cursor.execute(
            'update FISA_MEDICALA set DATA_REVENIRE_CONTROL = \'' + app.sheet.intrareDataRevenire + '\' where id = ' + app.sheet.idFisaDeModificat)
        app.cursor.execute('COMMIT')
        open_popup("Datele au fost modificate!")
    except cx_Oracle.DatabaseError as e:
        open_popupEror('Eroare! Data nu este valida!')
        traceback.print_exc()


def modificaFisa():
    app.sheet.idFisaDeModificat = app.sheet.modificareC.get()
    app.HideAll()
    app.sheet.ShowSheet(4)


def modificaFisaFull():
    app.patient.pacientC.set('')
    app.sheet.intrarePacient = app.sheet.strPacient.get()
    app.sheet.intrareMedic = app.sheet.strMedic.get()
    app.sheet.intrareDataEliberare = app.sheet.dataEliberareT.get("1.0", "end")
    app.sheet.intrareControlUrmator = app.sheet.revenireControlT.get("1.0", "end")
    app.sheet.intrareMedicamente = []
    for i in app.sheet.medicamenteLB.curselection():
        app.sheet.intrareMedicamente.append(i)
    app.sheet.intrareAfectiuni = []
    for i in app.sheet.afectiuniLB.curselection():
        app.sheet.intrareAfectiuni.append(i)
    app.sheet.intrareOrar = app.sheet.detaliiT.get("1.0", "end")

    for i in app.sheet.pacienti:
        if app.sheet.intrarePacient.__contains__(str(i)):
            app.sheet.intraretelefonPacient = i

    for i in app.sheet.psihiatri:
        if i[1] == app.sheet.intrareMedic:
            app.sheet.intraretelefonMedic = i[0]


def stergereFisa():
    nume = app.sheet.stergereC.get()
    app.patient.pacientC.set('')
    try:
        app.cursor.execute('delete from fisa_medicala where id = ' + nume)
        app.cursor.execute('commit')
        open_popup("Datele au fost sterse!")
        app.sheet.stergereC.set('')
    except IndexError as e:
        open_popupEror('Selectati un element de sters!\nCaseta vida!')
        traceback.print_exc()
    except cx_Oracle.DatabaseError as e:
        if str(e).__contains__('ORA-00936: missing expression'):
            open_popupEror('Selectati un element de sters!\nCaseta vida!')
        if str(e).__contains__('FISA_AFECTIUNE_FK_FISA_FK') or str(e).__contains__('FISA_FK_FISA_MEDICALA_FK'):
            app.cursor.execute('delete from MDT_FISA_FK where FISA_MEDICALA_ID = ' + nume)
            app.cursor.execute('commit')
            app.cursor.execute('delete from FISA_AFECTIUNE_FK where FISA_MEDICALA_ID = ' + nume)
            app.cursor.execute('commit')
            app.cursor.execute('delete from fisa_medicala where id = ' + nume)
            app.cursor.execute('commit')
            open_popup('Fisa medicala a fost stearsa!')
            selectOperatieFise(3)
            app.sheet.stergereC.set('')
    except Exception as e:
        traceback.print_exc()


def stergerePacient():
    nume = app.patient.pacientC.get()
    app.patient.pacientC.set('')
    for i in app.patient.pacienti:
        if nume.__contains__(str(i)):
            nr = i
            try:
                app.cursor.execute('delete from pacient where numar_telefon=' + str(nr))
                app.cursor.execute('commit')
                open_popup("Datele au fost sterse!")
                valori1 = []
                app.patient.pacienti = []
                app.cursor.execute('select nume,prenume,numar_telefon from pacient order by id asc')
                rows = app.cursor.fetchall()
                app.patient.pacienti = []
                for i in rows:
                    app.patient.pacienti.append(i[2])
                    valori1.append(str(i[0]) + ' ' + str(i[1]) + ' ' + str(i[2]))
                app.patient.pacientC['values'] = valori1  # se vor citi din baza de date
            except IndexError:
                open_popupEror('Selectati un element de sters!\nCaseta vida!')
            except cx_Oracle.IntegrityError:
                open_popupEror('Nu poate fi sters. Este referit in fise!')

def nextTablePacienti():
    app.patient.page += 1
    app.patient.HidePatient()
    app.patient.ShowPatient(0)
    printPacienti()


def prevTablePacienti():
    app.patient.page -= 1
    app.patient.HidePatient()
    app.patient.ShowPatient(0)
    printPacienti()


def afiseazaDoctor(e=None):
    nume = app.doctor.stergereC.get()
    for i in app.doctor.valori:
        if nume.__contains__(str(i)):
            try:
                app.cursor.execute('select * from psihiatru where numar_telefon = ' + str(i))
                date = app.cursor.fetchall()

                app.doctor.numeT.delete("1.0", "end")
                app.doctor.prenumeT.delete("1.0", "end")
                app.doctor.telefonT.delete("1.0", "end")
                app.doctor.emailT.delete("1.0", "end")

                app.doctor.numeT.insert(tk.END, date[0][1])
                app.doctor.prenumeT.insert(tk.END, date[0][2])
                app.doctor.telefonT.insert(tk.END, date[0][3])
                app.doctor.emailT.insert(tk.END, date[0][4])

            except Exception as e:
                print(e)
                traceback.print_exc()


def ModificareDoctor(e=None):
    nume = app.doctor.stergereC.get()
    app.doctor.stergereC.set('')
    for i in app.doctor.valori:
        if nume.__contains__(str(i)):
            id = i
            try:
                app.doctor.insertNume = app.doctor.numeT.get("1.0", "end")
                app.doctor.insertPrenume = app.doctor.prenumeT.get("1.0", "end")
                app.doctor.insertTelefon = app.doctor.telefonT.get("1.0", "end")
                app.doctor.insertEmail = app.doctor.emailT.get("1.0", "end")

                app.doctor.insertNume = app.doctor.insertNume.replace('\n', '')
                app.doctor.insertPrenume = app.doctor.insertPrenume.replace('\n', '')
                app.doctor.insertTelefon = app.doctor.insertTelefon.replace(' ', '').replace('\n', '')
                app.doctor.insertEmail = app.doctor.insertEmail.replace(' ', '').replace('\n', '')

                app.cursor.execute(
                    'update psihiatru set nume = \'' + app.doctor.insertNume + '\' where numar_telefon=' + str(id))
                app.cursor.execute(
                    'update psihiatru set prenume = \'' + app.doctor.insertPrenume + '\' where numar_telefon=' + str(
                        id))
                app.cursor.execute(
                    'update psihiatru set email = \'' + app.doctor.insertEmail + '\' where numar_telefon=' + str(id))
                app.cursor.execute(
                    'update psihiatru set numar_telefon = \'' + app.doctor.insertTelefon + '\' where numar_telefon=' + str(
                        id))
                app.cursor.execute('commit')
                open_popup("Datele au fost modificare!")
                valori1 = []
                app.doctor.valori = []
                app.cursor.execute('select nume,prenume,numar_telefon from psihiatru order by id asc')
                rows = app.cursor.fetchall()
                app.doctor.valori = []
                for i in rows:
                    app.doctor.valori.append(i[2])
                    valori1.append(str(i[0]) + ' ' + str(i[1]) + ' ' + str(i[2]))

                app.doctor.stergereC['values'] = valori1  # se vor citi din baza de date
                app.doctor.numeT.delete("1.0", "end")
                app.doctor.prenumeT.delete("1.0", "end")
                app.doctor.telefonT.delete("1.0", "end")
                app.doctor.emailT.delete("1.0", "end")
            except IndexError:
                open_popupEror('Selectati un element de sters!\nCaseta vida!')
            except Exception as e:
                if str(e).upper().__contains__('PSIHIATRU_NUMAR_TELEFON_CK'.upper()):
                    open_popupEror("Numarul de telefon este invalid!")
                elif str(e).upper().__contains__('ORA-12899'.upper()):
                    open_popupEror("Numarul de telefon este invalid!")
                elif str(e).upper().__contains__('PSIHIATRU_NUME_CK'.upper()):
                    open_popupEror("Numele nu este valid!")
                elif str(e).upper().__contains__('PSIHIATRU_PRENUME_CK'.upper()):
                    open_popupEror("Prenumele nu este valid!")
                elif str(e).upper().__contains__('PSIHIATRU_EMAIL_CK'.upper()):
                    open_popupEror("Email invalid!")
                traceback.print_exc()


def stergereDoctor(e=None):
    nume = app.doctor.stergereC.get()
    app.doctor.stergereC.set('')
    for i in app.doctor.valori:
        if nume.__contains__(str(i)):
            id = i
            try:
                app.cursor.execute('delete from psihiatru where numar_telefon=' + str(id))
                app.cursor.execute('commit')
                open_popup("Datele au fost sterse!")
                valori1 = []
                app.doctor.valori = []
                app.cursor.execute('select nume,prenume,numar_telefon from psihiatru order by id asc')
                rows = app.cursor.fetchall()
                app.doctor.valori = []
                for i in rows:
                    app.doctor.valori.append(i[2])
                    valori1.append(str(i[0]) + ' ' + str(i[1]) + ' ' + str(i[2]))
                app.doctor.stergereC['values'] = valori1  # se vor citi din baza de date
            except IndexError:
                open_popupEror('Selectati un element de sters!\nCaseta vida!')
            except cx_Oracle.IntegrityError:
                open_popupEror('Nu poate fi sters. Exista fise medicale scrise de el!')


def stergereDetalii():
    nume = app.details.afectiuneC.get()
    app.details.afectiuneC.set('')
    app.cursor.execute('select id from afectiune where nume = \'' + nume + '\'')
    id = app.cursor.fetchall()
    try:
        app.cursor.execute('delete from detalii_afectiune where afectiune_id=' + str(id[0][0]))
        app.cursor.execute('commit')
        open_popup("Datele au fost sterse!")
    except IndexError:
        open_popupEror('Selectati un element de sters!\nCaseta vida!')

    valori1 = []

    app.cursor.execute('select id,nume,afectiune_id from afectiune,detalii_afectiune where afectiune_id = id '
                       'order by nume asc')
    rows = app.cursor.fetchall()
    app.details.valori = []
    for i in rows:
        app.details.valori.append((i[0], i[1]))
        valori1.append(i[1])

    app.details.afectiuneC['values'] = valori1  # se vor citi din baza de date


def modificaMedicament():
    nume = app.drugs.stergereC.get()
    app.drugs.stergereC.set('')
    l = nume.split(' : ')
    try:
        if len(l) == 1:
            app.cursor.execute('select id from medicament where nume=\'' + l[0] + '\'')
        else:
            app.cursor.execute(
                'select id  from medicament where nume=\'' + l[0] + '\'and firma_producatoare = \'' + l[1] + '\'')
        rows = app.cursor.fetchall()
        app.drugs.inserareNume = app.drugs.numeT.get("1.0", "end")
        app.drugs.inserareFirma = app.drugs.firmaT.get("1.0", "end")

        app.drugs.inserareNume = app.drugs.inserareNume.replace('\n', '')
        app.drugs.inserareFirma = app.drugs.inserareFirma.replace('\n', '')
        app.cursor.execute(
            'update medicament set nume = \'' + app.drugs.inserareNume + '\' where id = ' + str(rows[0][0]))
        app.cursor.execute(
            'update medicament set firma_producatoare = \'' + app.drugs.inserareFirma + '\' where id = ' + str(
                rows[0][0]))
        app.drugs.numeT.delete('1.0', tk.END)
        app.drugs.firmaT.delete('1.0', tk.END)
        app.cursor.execute('commit')
        open_popup('Datele au fost modificate!')
    except IndexError:
        open_popupEror('Selectati un element de modificat!\nCaseta vida!')

    app.cursor.execute('select nume,firma_producatoare from medicament ')
    rows = app.cursor.fetchall()
    app.drugs.valori = []
    for i in rows:
        try:
            app.drugs.valori.append((i[0] + ' : ' + i[1]))
        except:
            app.drugs.valori.append((i[0]))

    app.drugs.stergereC['values'] = app.drugs.valori  # se vor citi din baza de date
    selectOperatieMedicament()


def stergereMedicament():
    nume = app.drugs.stergereC.get()
    app.drugs.stergereC.set('')
    l = nume.split(' : ')
    try:
        if len(l) == 1:
            app.cursor.execute('delete from medicament where nume=\'' + l[0] + '\'')
        else:
            app.cursor.execute(
                'delete from medicament where nume=\'' + l[0] + '\'and firma_producatoare = \'' + l[1] + '\'')
        app.cursor.execute('commit')
        open_popup("Datele au fost sterse!")
    except IndexError:
        open_popupEror('Selectati un element de sters!\nCaseta vida!')
    except cx_Oracle.IntegrityError:
        open_popupEror('Medicamentul nu poate fi sters!\nApare pe fisele medicale')

    app.cursor.execute('select nume,firma_producatoare from medicament ')
    rows = app.cursor.fetchall()
    app.drugs.valori = []
    for i in rows:
        try:
            app.drugs.valori.append((i[0] + ' : ' + i[1]))
        except Exception as e:
            app.drugs.valori.append((i[0]))

    app.drugs.stergereC['values'] = app.drugs.valori  # se vor citi din baza de date
    selectOperatieMedicament()


def stergereAfectiune():
    nume = app.disease.stergereC.get()
    app.disease.stergereC.set('')
    try:
        app.cursor.execute('delete from afectiune where nume=\'' + nume + '\'')
        app.cursor.execute('commit')
        open_popup("Datele au fost sterse!")
    except IndexError or cx_Oracle.DatabaseError:
        open_popupEror('Selectati un element de sters!\nCaseta vida!')
    except cx_Oracle.IntegrityError:
        open_popupEror(
            "Nu se poate sterge,\n deoarece este referita in fisele medicale sau exista detalii pentru ea definite.")

    app.cursor.execute('select nume from afectiune '
                       'order by nume asc')
    rows = app.cursor.fetchall()
    app.disease.valuesAf = []
    for i in rows:
        app.disease.valuesAf.append((i[0]))

    app.disease.stergereC['values'] = app.disease.valuesAf  # se vor citi din baza de date


def modificaAfectiune():
    nume = app.disease.stergereC.get()
    app.disease.stergereC.set('')
    app.disease.insertNume = app.disease.numeT.get('1.0', tk.END)
    try:
        app.cursor.execute('update  afectiune set nume = \'' + app.disease.insertNume + '\'where nume=\'' + nume + '\'')
        app.cursor.execute('commit')
        open_popup("Datele au fost modificate!")
    except IndexError or cx_Oracle.DatabaseError:
        open_popupEror('Selectati un element de modificat!\nCaseta vida!')

    app.cursor.execute('select nume from afectiune '
                       'order by nume asc')
    rows = app.cursor.fetchall()
    app.disease.valuesAf = []
    for i in rows:
        app.disease.valuesAf.append((i[0]))

    app.disease.stergereC['values'] = app.disease.valuesAf  # se vor citi din baza de date


def nextTableDetalii():
    app.details.page += 1
    app.details.HideDetails()
    app.details.ShowDetails(0)
    printDetalii()


def prevTableDetalii():
    app.details.page -= 1
    app.details.HideDetails()
    app.details.ShowDetails(0)
    printDetalii()


def nextTableDoctor():
    app.doctor.page += 1
    app.doctor.HideDoctor()
    app.doctor.ShowDoctor(0)
    printPsihiatri()


def prevTableDoctor():
    app.doctor.page -= 1
    app.doctor.HideDoctor()
    app.doctor.ShowDoctor(0)
    printPsihiatri()


def nextTableAfectiuni():
    app.disease.page += 1
    app.disease.HideDisease()
    app.disease.ShowDisease(0)
    printAfectiuni()


def prevTableAfectiuni():
    app.disease.page -= 1
    app.disease.HideDisease()
    app.disease.ShowDisease(0)
    printAfectiuni()


def nextTableFisa():
    app.sheet.page += 1
    app.sheet.HideSheet()
    app.sheet.ShowSheet(0)
    printFise()


def prevTableFisa():
    app.sheet.page -= 1
    app.sheet.HideSheet()
    app.sheet.ShowSheet(0)
    printFise()


def nextTableMedicamente():
    app.drugs.page += 1
    app.drugs.HideDrugs()
    app.drugs.ShowDrugs(0)
    printDrugs()


def prevTableMedicamente():
    app.drugs.page -= 1
    app.drugs.HideDrugs()
    app.drugs.ShowDrugs(0)
    printDrugs()


def selectOperatieFise(e=None):
    app.HideAll()
    app.cursor.execute('select  nume , prenume,numar_telefon  from pacient order by id asc')
    rows = app.cursor.fetchall()
    app.sheet.pacienti = []
    pacienti1 = []

    for i in rows:
        app.sheet.pacienti.append((i[2]))
        pacienti1.append((i[0] + ' ' + i[1] + ' ' + i[2]))

    app.sheet.pacientC['state'] = 'readonly'
    app.sheet.pacientC['values'] = pacienti1
    app.sheet.valori = []
    app.cursor.execute('select id from fisa_medicala order by id asc')
    rows = app.cursor.fetchall()
    app.sheet.valori = []
    for i in rows:
        app.sheet.valori.append(i[0])

    app.patient.pacientC['values'] = app.sheet.valori  # se vor citi din baza de date

    if app.sheet.strOperatie.get() == ' Adaugare':
        app.sheet.ShowSheet(1)
    elif app.sheet.strOperatie.get() == ' Vizualizare':
        app.sheet.ShowSheet(0)
        printFise()
    elif app.sheet.strOperatie.get() == ' Modificare':
        app.cursor.execute('select id from fisa_medicala')
        rows = app.cursor.fetchall()

        for i in rows:
            app.sheet.valori.append(i[0])

        app.sheet.modificareC['values'] = app.sheet.valori  # se vor citi din baza de date
        app.sheet.ShowSheet(2)
    else:
        app.cursor.execute('select id from fisa_medicala')
        rows = app.cursor.fetchall()

        for i in rows:
            app.sheet.valori.append(i[0])

        app.sheet.stergereC['values'] = app.sheet.valori  # se vor citi din baza de date
        app.sheet.ShowSheet(3)


def printPacienti():
    try:
        app.cursor.execute('select * from pacient order by ID')
        rows = app.cursor.fetchall()
        name = ('ID', 'Nume', 'Prenume', 'Data nasterii', 'Numar de telefon', 'Gen', 'Adresa')
        # print(rows)

        nrelemente = int(app.height / (12 * 3))

        if app.patient.page > (len(rows) / nrelemente):
            app.patient.page = int(len(rows) / nrelemente)

        if app.patient.page < 0:
            app.patient.page = 0

        indexStat = app.patient.page * nrelemente
        indexStop = app.patient.page * nrelemente + nrelemente

        if indexStop > len(rows):
            indexStop = len(rows)

        for j in range(7):
            e = tk.Entry(app, width=(int(app.width / 50)), fg='blue', font=('Arial', 12, 'bold'), background='white')
            e.place(x=j * (app.width / 10) + (app.width / 10), y=((app.height / 3) - (int(app.width / 100))))
            e.insert(tk.END, name[j])
            app.patient.table.append(e)

        for i in range(indexStat, indexStop):
            for j in range(7):
                if j == 6:
                    e = tk.Entry(app, width=(int(app.width / 50)), fg='LightBlue', font=('Arial', 12, 'bold'),
                                 background='gray10')
                else:
                    e = tk.Entry(app, width=(int(app.width / 90)), fg='LightBlue', font=('Arial', 12, 'bold'),
                                 background='gray10')
                e.place(x=j * (app.width / 10) + (app.width / 10),
                        y=(i % nrelemente) * (int(app.width / 100)) + (app.height / 3))
                try:
                    if j == 3:
                        e.insert(tk.END, rows[i][j].strftime("%d-%m-%Y"))
                    else:
                        e.insert(tk.END, rows[i][j])
                    app.patient.table.append(e)
                except:
                    app.patient.table.append(e)

    except cx_Oracle.DatabaseError as e:
        print(e)


def printFise():
    app.cursor.execute('select fisa_medicala.id,fisa_medicala.data_eliberare,fisa_medicala.data_revenire_control,'
                       'pacient.nume,pacient.prenume,psihiatru.nume,psihiatru.prenume from '
                       'fisa_medicala,psihiatru,pacient where fisa_medicala.pacient_id = pacient.id and '
                       'fisa_medicala.psihiatru_id = psihiatru.id order by fisa_medicala.id ')
    rows = app.cursor.fetchall()
    name = ('ID', 'Data eliberare', 'Data revenire control', 'Nume pacient', 'Prenume pacient', 'Nume medic',
            'Prenume medic', 'Prescriptie medicala','Afectiuni')

    nrelemente = int(app.height / (12 * 3))

    if app.sheet.page > (len(rows) / nrelemente):
        app.sheet.page = int(len(rows) / nrelemente)

    if app.sheet.page < 0:
        app.sheet.page = 0

    indexStat = app.sheet.page * nrelemente
    indexStop = app.sheet.page * nrelemente + nrelemente

    if indexStop > len(rows):
        indexStop = len(rows)

    for j in range(9):
        e = tk.Entry(app, width=(int(app.width / 100)), fg='blue', font=('Arial', 12, 'bold'), background='white')
        e.place(x=j * (app.width / 12) + (app.width / 20), y=((app.height / 3) - (int(app.width / 100))))
        e.insert(tk.END, name[j])
        app.sheet.table.append(e)

    for i in range(indexStat, indexStop):
        for j in range(9):
            if j == 7:
                e = tk.Entry(app, width=(int(app.width / 100)), fg='LightBlue', font=('Arial', 12, 'bold'),
                             background='gray10')
                e.place(x=j * (app.width / 12) + (app.width / 20),
                        y=(i % nrelemente) * (int(app.width / 100)) + (app.height / 3))
                app.cursor.execute(
                    'select nume,numar_de_bucati_zilnic,descriere_orar from medicament, MDT_FISA_FK where id = '
                    'MDT_FISA_FK.medicament_id and MDT_FISA_FK.FISA_MEDICALA_ID = ' + str(rows[i][0]))
                line = app.cursor.fetchall()
                sir = ''
                for z in line:
                    sir = '[' + z[0] + '(' + str(z[1]) + ') ' + ' : ' + z[2] + ']'
                    e.insert(tk.END, sir)
                    app.sheet.table.append(e)
            elif j == 8:
                e = tk.Entry(app, width=(int(app.width / 100)), fg='LightBlue', font=('Arial', 12, 'bold'),
                             background='gray10')
                e.place(x=j * (app.width / 12) + (app.width / 20),
                        y=(i % nrelemente) * (int(app.width / 100)) + (app.height / 3))
                app.cursor.execute(
                    'select nume from afectiune, fisa_afectiune_fk where id = '
                    'fisa_afectiune_fk.afectiune_id and fisa_afectiune_fk.FISA_MEDICALA_ID = ' + str(rows[i][0]))
                line = app.cursor.fetchall()
                sir = ''
                for z in line:
                    e.insert(tk.END, z)
                    e.insert(tk.END,' ')
                    app.sheet.table.append(e)
            else:
                e = tk.Entry(app, width=(int(app.width / 100)), fg='LightBlue', font=('Arial', 12, 'bold'),
                             background='gray10')
                e.place(x=j * (app.width / 12) + (app.width / 20),
                        y=(i % nrelemente) * (int(app.width / 100)) + (app.height / 3))
            try:
                if j == 1 or j == 2:
                    e.insert(tk.END, rows[i][j].strftime("%d-%m-%Y"))
                else:
                    e.insert(tk.END, rows[i][j])
                app.sheet.table.append(e)
            except Exception as ex:
                app.sheet.table.append(e)


def printDrugs():
    app.cursor.execute('select * from medicament order by id asc')
    rows = app.cursor.fetchall()
    name = ('ID', 'Nume medicament', 'Firma producatoare')

    nrelemente = int(app.height / (12 * 3))

    if app.drugs.page > (len(rows) / nrelemente):
        app.drugs.page = int(len(rows) / nrelemente)

    if app.drugs.page < 0:
        app.drugs.page = 0

    indexStat = app.drugs.page * nrelemente
    indexStop = app.drugs.page * nrelemente + nrelemente

    if indexStop > len(rows):
        indexStop = len(rows)

    for j in range(3):
        e = tk.Entry(app, width=(int(app.width / 50)), fg='blue', font=('Arial', 12, 'bold'), background='white')
        e.place(x=j * (app.width / 6) + (app.width / 20), y=((app.height / 3) - (int(app.width / 100))))
        e.insert(tk.END, name[j])
        app.drugs.table.append(e)

    for i in range(indexStat, indexStop):
        for j in range(3):
            e = tk.Entry(app, width=(int(app.width / 50)), fg='LightBlue', font=('Arial', 12, 'bold'),
                         background='gray10')
            e.place(x=j * (app.width / 6) + (app.width / 20),
                    y=(i % nrelemente) * (int(app.width / 100)) + (app.height / 3))
            try:
                e.insert(tk.END, rows[i][j])
                app.drugs.table.append(e)
            except:
                app.drugs.table.append(e)


def printAfectiuni():
    app.cursor.execute('select * from afectiune order by id asc')
    rows = app.cursor.fetchall()
    name = ('ID', 'Nume afectiune')

    nrelemente = int(app.height / (12 * 3))

    if app.disease.page > (len(rows) / nrelemente):
        app.disease.page = int(len(rows) / nrelemente)

    if app.disease.page < 0:
        app.disease.page = 0

    indexStat = app.disease.page * nrelemente
    indexStop = app.disease.page * nrelemente + nrelemente

    if indexStop > len(rows):
        indexStop = len(rows)

    for j in range(2):
        e = tk.Entry(app, width=(int(app.width / 50)), fg='blue', font=('Arial', 12, 'bold'), background='white')
        e.place(x=j * (app.width / 6) + (app.width / 20), y=((app.height / 3) - (int(app.width / 100))))
        e.insert(tk.END, name[j])
        app.disease.table.append(e)

    for i in range(indexStat, indexStop):
        for j in range(2):
            e = tk.Entry(app, width=(int(app.width / 50)), fg='LightBlue', font=('Arial', 12, 'bold'),
                         background='gray10')
            e.place(x=j * (app.width / 6) + (app.width / 20),
                    y=(i % nrelemente) * (int(app.width / 100)) + (app.height / 3))
            try:
                e.insert(tk.END, rows[i][j])
                app.disease.table.append(e)
            except:
                app.disease.table.append(e)


def printDetalii():
    app.cursor.execute('select afectiune.nume, detalii_afectiune.gravitate, detalii_afectiune.rata_vindecare, '
                       'detalii_afectiune.tratament_medicamentos, detalii_afectiune.rata_suicid, '
                       'detalii_afectiune.specificitate_gen, detalii_afectiune.asistenta_personala from afectiune , '
                       'detalii_afectiune  where detalii_afectiune.afectiune_id = afectiune.id order by afectiune.id')
    rows = app.cursor.fetchall()
    name = ('Nume afecitune', 'Gravitate', 'Rata vindecare', 'Medicamente', 'Rata suicid', 'Specificitate gen',
            'Asistenta personala')

    nrelemente = int(app.height / (12 * 3))

    if app.details.page > (len(rows) / nrelemente):
        app.details.page = int(len(rows) / nrelemente)

    if app.details.page < 0:
        app.details.page = 0

    indexStat = app.details.page * nrelemente
    indexStop = app.details.page * nrelemente + nrelemente

    if indexStop > len(rows):
        indexStop = len(rows)

    for j in range(7):
        e = tk.Entry(app, width=(int(app.width / 100)), fg='blue', font=('Arial', 12, 'bold'), background='white')
        e.place(x=j * (app.width / 12) + (app.width / 20), y=((app.height / 3) - (int(app.width / 100))))
        e.insert(tk.END, name[j])
        app.details.table.append(e)

    for i in range(indexStat, indexStop):
        for j in range(7):
            e = tk.Entry(app, width=(int(app.width / 100)), fg='LightBlue', font=('Arial', 12, 'bold'),
                         background='gray10')
            e.place(x=j * (app.width / 12) + (app.width / 20),
                    y=(i % nrelemente) * (int(app.width / 100)) + (app.height / 3))
            try:
                e.insert(tk.END, rows[i][j])
                app.details.table.append(e)
            except:
                app.details.table.append(e)


def printPsihiatri():
    app.cursor.execute('select * from psihiatru order by id asc')
    rows = app.cursor.fetchall()
    name = ('ID', 'Nume ', 'Prenume', 'Telefon', 'Email')

    nrelemente = int(app.height / (12 * 3))

    if app.doctor.page > (len(rows) / nrelemente):
        app.doctor.page = int(len(rows) / nrelemente)

    if app.doctor.page < 0:
        app.doctor.page = 0

    indexStat = app.doctor.page * nrelemente
    indexStop = app.doctor.page * nrelemente + nrelemente

    if indexStop > len(rows):
        indexStop = len(rows)

    for j in range(5):
        e = tk.Entry(app, width=(int(app.width / 50)), fg='blue', font=('Arial', 12, 'bold'), background='white')
        e.place(x=j * (app.width / 6) + (app.width / 20), y=((app.height / 3) - (int(app.width / 100))))
        e.insert(tk.END, name[j])
        app.doctor.table.append(e)

    for i in range(indexStat, indexStop):
        for j in range(5):
            e = tk.Entry(app, width=(int(app.width / 50)), fg='LightBlue', font=('Arial', 12, 'bold'),
                         background='gray10')
            e.place(x=j * (app.width / 6) + (app.width / 20),
                    y=(i % nrelemente) * (int(app.width / 100)) + (app.height / 3))
            try:
                e.insert(tk.END, rows[i][j])
                app.doctor.table.append(e)
            except:
                app.doctor.table.append(e)


def selectOperatiePacient(e=None):
    valori1 = []
    app.patient.pacienti = []
    app.cursor.execute('select nume,prenume,numar_telefon from pacient order by id asc')
    rows = app.cursor.fetchall()
    app.patient.pacienti = []
    for i in rows:
        app.patient.pacienti.append(i[2])
        valori1.append(str(i[0]) + ' ' + str(i[1]) + ' ' + str(i[2]))

    app.patient.pacientC['values'] = valori1  # se vor citi din baza de date
    app.HideAll()
    if app.patient.strOperatie.get() == ' Adaugare':
        app.patient.numeT.delete("0.0", "end")
        app.patient.prenumeT.delete("0.0", "end")
        app.patient.dataNasteriiT.delete("0.0", "end")
        app.patient.telefonT.delete("0.0", "end")
        app.patient.adresaT.delete("0.0", "end")
        app.patient.genC.set('')
        app.patient.ShowPatient(1)
    elif app.patient.strOperatie.get() == ' Vizualizare':
        app.patient.ShowPatient(0)
        printPacienti()
    elif app.patient.strOperatie.get() == ' Modificare':
        app.patient.numeT.delete("0.0", "end")
        app.patient.prenumeT.delete("0.0", "end")
        app.patient.dataNasteriiT.delete("0.0", "end")
        app.patient.telefonT.delete("0.0", "end")
        app.patient.adresaT.delete("0.0", "end")
        app.patient.genC.set('')
        app.patient.ShowPatient(2)
    else:
        app.patient.ShowPatient(3)


def selectOperatieMedicament(e=None):
    app.HideAll()
    app.drugs.valori = []
    app.cursor.execute('select nume,firma_producatoare from medicament')
    rows = app.cursor.fetchall()

    for i in rows:
        try:
            app.drugs.valori.append(i[0] + ' : ' + i[1])
        except TypeError:
            app.drugs.valori.append(i[0])

    app.drugs.stergereC['values'] = app.drugs.valori  # se vor citi din baza de date
    if app.drugs.strOperatie.get() == ' Adaugare':
        app.drugs.numeT.delete('1.0', tk.END)
        app.drugs.firmaT.delete('1.0', tk.END)
        app.drugs.ShowDrugs(1)
    elif app.drugs.strOperatie.get() == ' Vizualizare':
        app.drugs.numeT.delete('1.0', tk.END)
        app.drugs.firmaT.delete('1.0', tk.END)
        app.drugs.ShowDrugs(0)
        printDrugs()
    elif app.drugs.strOperatie.get() == ' Modificare':
        app.drugs.ShowDrugs(2)
    else:
        app.drugs.ShowDrugs(3)


def selectSpecificitateGenDetalii(e=None):
    pass


def selectOperatieAfectiune(e=None):
    app.HideAll()
    if app.disease.strOperatie.get() == ' Adaugare':
        app.disease.ShowDisease(1)
    elif app.disease.strOperatie.get() == ' Vizualizare':
        app.disease.ShowDisease(0)
        printAfectiuni()
    elif app.disease.strOperatie.get() == ' Modificare':
        app.disease.ShowDisease(2)
        app.cursor.execute('select * from afectiune order by ID')
        rows = app.cursor.fetchall()

        app.disease.valuesAf = []

        for i in rows:
            app.disease.valuesAf.append(str(i[1]))
        app.disease.stergereC['values'] = app.disease.valuesAf  # se vor citi din baza de date

    else:
        app.disease.ShowDisease(3)
        app.cursor.execute('select nume from afectiune '
                           'order by nume asc')
        rows = app.cursor.fetchall()
        app.disease.valuesAf = []
        for i in rows:
            app.disease.valuesAf.append((i[0]))

        app.disease.stergereC['values'] = app.disease.valuesAf  # se vor citi din baza de date


def selectOperatiePsihiatru(e=None):
    app.HideAll()
    if app.doctor.strOperatie.get() == ' Adaugare':
        app.doctor.ShowDoctor(1)
    elif app.doctor.strOperatie.get() == ' Vizualizare':
        app.doctor.ShowDoctor(0)
        printPsihiatri()
    elif app.doctor.strOperatie.get() == ' Modificare':
        valori1 = []
        app.doctor.valori = []
        app.cursor.execute('select nume,prenume,numar_telefon from psihiatru order by id asc')
        rows = app.cursor.fetchall()
        app.doctor.valori = []
        for i in rows:
            app.doctor.valori.append(i[2])
            valori1.append(str(i[0]) + ' ' + str(i[1]) + ' ' + str(i[2]))

        app.doctor.stergereC['values'] = valori1  # se vor citi din baza de date
        app.doctor.ShowDoctor(2)
    else:
        valori1 = []
        app.doctor.valori = []
        app.cursor.execute('select nume,prenume,numar_telefon from psihiatru order by id asc')
        rows = app.cursor.fetchall()
        app.doctor.valori = []
        for i in rows:
            app.doctor.valori.append(i[2])
            valori1.append(str(i[0]) + ' ' + str(i[1]) + ' ' + str(i[2]))

        app.doctor.stergereC['values'] = valori1  # se vor citi din baza de date
        app.doctor.ShowDoctor(3)


def selectOperatieDetalii(e=None):
    app.HideAll()
    if app.details.strOperatie.get() == ' Adaugare':
        valori1 = []

        app.cursor.execute('select id,nume from afectiune order by id asc')
        rows = app.cursor.fetchall()

        for i in rows:
            app.details.valori.append((i[0], i[1]))
            valori1.append(i[1])

        app.details.afectiuneC['values'] = valori1  # se vor citi din baza de date

        app.details.ShowDetails(1)
        app.details.operatieC.current(1)
    elif app.details.strOperatie.get() == ' Vizualizare':
        app.details.ShowDetails(0)
        app.details.operatieC.current(0)
        printDetalii()
    elif app.details.strOperatie.get() == ' Modificare':

        valori1 = []

        app.cursor.execute('select id,nume,afectiune_id from afectiune,detalii_afectiune where afectiune_id = id '
                           'order by nume asc')
        rows = app.cursor.fetchall()

        for i in rows:
            app.details.valori.append((i[0], i[1]))
            valori1.append(i[1])

        app.details.afectiuneC['values'] = valori1  # se vor citi din baza de date
        app.details.ShowDetails(2)
        app.details.operatieC.current(2)
    else:
        app.details.ShowDetails(3)
        valori1 = []

        app.cursor.execute('select id,nume,afectiune_id from afectiune,detalii_afectiune where afectiune_id = id '
                           'order by nume asc')
        rows = app.cursor.fetchall()

        for i in rows:
            app.details.valori.append((i[0], i[1]))
            valori1.append(i[1])

        app.details.afectiuneC['values'] = valori1  # se vor citi din baza de date
        app.details.operatieC.current(3)


def selectAsistentaPersonalaDetalii(e=None):
    pass


def okpopup():
    app.top.destroy()
    app.grab_set()


def open_popupEror(tip):
    # playsound('mySound.mp3')
    app.top = tk.Toplevel(app, background='red')
    app.top.geometry('400x200')
    app.top.title("Eroare")
    tk.Label(app.top, text=tip, background='red', font=('Mistral 12 bold')).place(relx=0.5, rely=0.2, anchor='center')
    tk.Button(app.top, text="OK", font='Mistral 12 bold', background='white', command=okpopup).place(relx=0.5, rely=0.5,
                                                                                                     anchor='center')
    app.eval(f'tk::PlaceWindow {str(app.top)} center')


def open_popup(tip):
    app.top = tk.Toplevel(app, background='green')
    app.top.geometry('400x200')
    app.top.title("Mesaj")
    tk.Label(app.top, text=tip, background='green', font=('Mistral 12 bold')).place(relx=0.5, rely=0.2, anchor='center')
    tk.Button(app.top, text="OK", font=('Mistral 12 bold'), background='white', command=okpopup).place(relx=0.5,
                                                                                                       rely=0.5,
                                                                                                       anchor='center')
    app.eval(f'tk::PlaceWindow {str(app.top)} center')


def open_popupServer():
    app.top = tk.Toplevel(app, background='red')
    app.top.geometry('400x200')
    app.top.title("Mesaj")
    tk.Label(app.top, text="Aplicatia nu se poate conecta la server!\nContactati un administrator!", background='red',
             font=('Mistral 12 bold')).place(relx=0.5, rely=0.2, anchor='center')
    tk.Button(app.top, text="OK", font=('Mistral 12 bold'), background='white', command=okpopup).place(relx=0.5,
                                                                                                       rely=0.5,
                                                                                                       anchor='center')
    app.eval(f'tk::PlaceWindow {str(app.top)} center')


def adaugaFisa(e=None):
    ok = 1
    idpacient = None
    app.sheet.intrarePacient = app.sheet.strPacient.get()
    app.sheet.intrareMedic = app.sheet.strMedic.get()
    app.sheet.intrareDataEliberare = app.sheet.dataEliberareT.get("1.0", "end")
    app.sheet.intrareControlUrmator = app.sheet.revenireControlT.get("1.0", "end")
    app.sheet.intrareMedicamente = []
    cantitate = app.sheet.cantitateT.get("1.0", "end")
    for i in app.sheet.medicamenteLB.curselection():
        app.sheet.intrareMedicamente.append(i)
    app.sheet.intrareAfectiuni = []
    for i in app.sheet.afectiuniLB.curselection():
        app.sheet.intrareAfectiuni.append(i)
    app.sheet.intrareOrar = app.sheet.detaliiT.get("1.0", "end")

    for i in app.sheet.pacienti:
        if app.sheet.intrarePacient.__contains__(str(i)):
            app.sheet.intrareIDPacient = i

    for i in app.sheet.psihiatri:
        if i[1] == app.sheet.intrareMedic:
            app.sheet.intrareIDMedic = i[0]

    app.cursor.execute('ALTER SESSION SET NLS_DATE_FORMAT = \'DD-MM-YYYY\'')

    try:
        app.cursor.execute('select id from pacient where numar_telefon = ' + str(app.sheet.intrareIDPacient))
        idpacient = app.cursor.fetchall()
        app.cursor.execute(
            'insert into fisa_medicala values(null,\'' + app.sheet.intrareDataEliberare + '\',\'' + app.sheet.intrareControlUrmator + '\',' + str(
                idpacient[0][0]) + ', ' + str(app.sheet.intrareIDMedic) + ')')
        app.cursor.execute('commit')
    except Exception as e:
        ok = 0
        open_popupEror('Valorile nu sunt valide verificati!')
        if str(e).upper().__contains__('column not allowed here'.upper()):
            app.cursor.execute(
                'insert into fisa_medicala values(null,\'' + app.sheet.intrareDataEliberare + '\',' + 'null' + ',' + str(
                    idpacient[0][0]) + ', ' + str(app.sheet.intrareIDMedic) + ')')
        else:
            print("Eroare la inserare in fisa medicala " + str(e))
            traceback.print_exc()

    listMed = []
    for i in app.sheet.intrareMedicamente:
        listMed.append(app.sheet.valuesMed[i][0])
    cantitati = cantitate.split('\n')
    programe = app.sheet.intrareOrar.split('\n')
    cantitati.remove('')
    programe.remove('')

    try:
        for i in range(len(listMed)):
            app.cursor.execute('select max(ID) from fisa_medicala')
            ind = app.cursor.fetchall()
            app.cursor.execute('insert into MDT_FISA_FK values(' + str(listMed[i]) + ', ' + str(ind[0][0]) + ',' + str(
                int(cantitati[i])) + ',\'' + programe[i] + '\')')
            app.cursor.execute('commit')
    except Exception as e:
        ok = 0
        open_popupEror('Datele introduse nu sunt valide!')

    listAfectiuni = []
    for i in app.sheet.intrareAfectiuni:
        listAfectiuni.append(app.sheet.valuesAf[i][0])
    try:
        for i in listAfectiuni:
            app.cursor.execute('select max(ID) from fisa_medicala')
            ind = app.cursor.fetchall()
            app.cursor.execute('insert into FISA_AFECTIUNE_FK values(' + str(ind[0][0]) + ', ' + str(i) + ')')
            app.cursor.execute('commit')
    except Exception as e:
        print(e)
        ok = 0
    if ok == 1:
        open_popup("Datele au fost inserate cu success!")
        app.sheet.dataEliberareT.delete("1.0", "end")
        app.sheet.revenireControlT.delete("1.0", "end")
        app.sheet.pacientC.set('')
        app.sheet.medicC.set('')
        app.sheet.afectiuniLB.select_clear(0, tk.END)
        app.sheet.medicamenteLB.select_clear(0, tk.END)
        app.sheet.cantitateT.delete("1.0", "end")
        app.sheet.detaliiT.delete("1.0", "end")



def adaugaPacient(e=None):
    app.patient.inserareNume = app.patient.numeT.get("1.0", "end")
    app.patient.inserarePrenume = app.patient.prenumeT.get("1.0", "end")
    app.patient.inserareDataNastere = app.patient.dataNasteriiT.get("1.0", "end")
    app.patient.inserareTelefon = app.patient.telefonT.get("1.0", "end")
    app.patient.inserareGen = app.patient.genC.get()
    app.patient.inserareAdresa = app.patient.adresaT.get("1.0", "end")

    app.patient.inserareNume = app.patient.inserareNume.replace('\n', '')
    app.patient.inserarePrenume = app.patient.inserarePrenume.replace('\n', '')
    app.patient.inserareDataNastere = app.patient.inserareDataNastere.replace(' ', '').replace('\n', '')
    app.patient.inserareTelefon = app.patient.inserareTelefon.replace(' ', '').replace('\n', '')
    app.patient.inserareGen = app.patient.inserareGen.replace(' ', '').replace('\n', '')
    app.patient.inserareAdresa = app.patient.inserareAdresa.replace('\n', '')

    app.cursor.execute('ALTER SESSION SET NLS_DATE_FORMAT = \'DD-MM-YYYY\'')

    try:
        app.cursor.execute(
            'insert into pacient values(null,\'' + app.patient.inserareNume + '\',\'' + app.patient.inserarePrenume + '\',\'' + app.patient.inserareDataNastere + '\', \'' + app.patient.inserareTelefon + '\',\'' + app.patient.inserareGen + '\',\' ' + app.patient.inserareAdresa + '\')')
        open_popup("Datele au fost introduse!")
        app.cursor.execute('commit')
        app.patient.numeT.delete("0.0", "end")
        app.patient.prenumeT.delete("0.0", "end")
        app.patient.dataNasteriiT.delete("0.0", "end")
        app.patient.telefonT.delete("0.0", "end")
        app.patient.adresaT.delete("0.0", "end")
        app.patient.genC.set('')
    except cx_Oracle.IntegrityError as e:
        if str(e).__contains__('PACIENT_NUME_CK'.upper()):
            open_popupEror("Numele este gresit!")
        elif str(e).__contains__('PACIENT_Prenume_CK'.upper()):
            open_popupEror("Prenumele este gresit!")
        elif str(e).__contains__('pacient_gen'.upper()):
            open_popupEror("Genul este gresit!")
        elif str(e).__contains__('pacient_numar_telefon_ck'.upper()):
            open_popupEror("Numarul de telefon este invalid!")
        elif str(e).upper().__contains__('ORA-01400'.upper()):
            open_popupEror("Exista campuri vide!")
        elif str(e).upper().__contains__('ORA-01843: not a valid month'.upper()):
            open_popupEror("Luna nu este valida!")
        print(e)
        traceback.print_exc()
    except cx_Oracle.DatabaseError as e:
        if str(e).__contains__('TRG_PACIENT_BRIU'.upper()):
            open_popupEror("Data de nastere invalida.\n Trebuie sa fie mai mica decat data curenta!")
        elif str(e).upper().__contains__('ORA-01830'.upper()) or str(e).upper().__contains__('ORA-01858'.upper()):
            open_popupEror("Format data necorespunzator.\nFolositi DD-MM-YYYY!")
        elif str(e).upper().__contains__('ORA-01843: not a valid month'.upper()):
            open_popupEror("Luna nu este valida!")
        elif str(e).upper().__contains__(
                'ORA-12899: value too large for column "BD034"."PACIENT"."NUMAR_TELEFON"'.upper()):
            open_popupEror("Numarul de telefon nu este valid!")
        traceback.print_exc()
    except Exception as e:
        print(e)
        traceback.print_exc()


def adaugaMedicament(e=None):
    app.drugs.inserareNume = app.drugs.numeT.get("1.0", "end")
    app.drugs.inserareFirma = app.drugs.firmaT.get("1.0", "end")

    app.drugs.inserareNume = app.drugs.inserareNume.replace('\n', '')
    app.drugs.inserareFirma = app.drugs.inserareFirma.replace('\n', '')

    try:
        app.cursor.execute(
            'insert into medicament values(null,\'' + app.drugs.inserareNume + '\',\'' + app.drugs.inserareFirma + '\')')
        open_popup("Datele au fost introduse!")
        app.cursor.execute('commit')
        app.drugs.numeT.delete('1.0', tk.END)
        app.drugs.firmaT.delete('1.0', tk.END)
    except cx_Oracle.IntegrityError as e:
        if str(e).upper().__contains__('ORA-01400'.upper()):
            open_popupEror("Campul \'Nume\' nu poate fi vid!")
        if str(e).upper().__contains__('MDT_NUME_FIRMA_PRODUCATOARE_UN'.upper()):
            open_popupEror("Numele medicamentului este deja in baza de date!")
        print(e)
        traceback.print_exc()
    except Exception as e:
        print(e)
        traceback.print_exc()


def adaugareAfectiune(e=None):
    app.disease.insertNume = app.disease.numeT.get("1.0", "end")

    app.disease.insertNume = app.disease.insertNume.replace('\n', '')

    try:
        app.cursor.execute('insert into afectiune values(null,\'' + app.disease.insertNume + '\')')
        open_popup("Datele au fost introduse")
        app.cursor.execute('commit')

    except cx_Oracle.IntegrityError as e:
        if str(e).upper().__contains__('AFECTIUNE_NUME_CK'.upper()):
            open_popupEror("Numele afectiuni este invalid!")
        if str(e).upper().__contains__('AFECTIUNE_NUME_UN'.upper()):
            open_popupEror("Numele afectiuni este deja in baza de date!")
        print(e)
    except Exception as e:
        print(e)


def adaugaDoctor(e=None):
    app.doctor.insertNume = app.doctor.numeT.get("1.0", "end")
    app.doctor.insertPrenume = app.doctor.prenumeT.get("1.0", "end")
    app.doctor.insertTelefon = app.doctor.telefonT.get("1.0", "end")
    app.doctor.insertEmail = app.doctor.emailT.get("1.0", "end")

    app.doctor.insertNume = app.doctor.insertNume.replace('\n', '')
    app.doctor.insertPrenume = app.doctor.insertPrenume.replace('\n', '')
    app.doctor.insertTelefon = app.doctor.insertTelefon.replace(' ', '').replace('\n', '')
    app.doctor.insertEmail = app.doctor.insertEmail.replace(' ', '').replace('\n', '')

    try:
        app.cursor.execute(
            'insert into psihiatru values(null,\'' + app.doctor.insertNume + '\',\'' + app.doctor.insertPrenume + '\',\'' + app.doctor.insertTelefon + '\', \'' + app.doctor.insertEmail + '\')')
        open_popup("Datele au fost intoduse!")
        app.cursor.execute('commit')

        app.doctor.numeT.delete("1.0", "end")
        app.doctor.prenumeT.delete("1.0", "end")
        app.doctor.telefonT.delete("1.0", "end")
        app.doctor.emailT.delete("1.0", "end")

    except Exception as e:
        if str(e).upper().__contains__('psihiatru_email_ck'.upper()):
            open_popupEror("Emailul este invalid!")
        elif str(e).upper().__contains__('psihiatru_numar_telefon_ck'.upper()):
            open_popupEror("Numarul de telefon este invalid!")
        elif str(e).upper().__contains__('psihiatru_prenume_ck'.upper()):
            open_popupEror("Prenumele este gresit!")
        elif str(e).upper().__contains__('psihiatru_nume_ck'.upper()):
            open_popupEror("Numele este gresit!")
        elif str(e).upper().__contains__('ORA-01400'.upper()):
            open_popupEror("Exista campuri vide!")
        elif str(e).upper().__contains__('value too large for colum"'.upper()):
            open_popupEror("Un camp are o valoare prea mare!")
        print(e)
        traceback.print_exc()


def modificaDetalii():
    app.details.inserareGravitate = app.details.gravitateT.get("1.0", "end")
    app.details.inserareRataVindecare = app.details.vindecareT.get("1.0", "end")
    app.details.inserareTratament = app.details.tratamentC.get()
    app.details.inserareRataSuicid = app.details.suicidT.get("1.0", "end")
    app.details.inserareSpecificitateGen = app.details.specificitateC.get()
    app.details.inserareAsistentaPersonala = app.details.asistentaPersonalaC.get()
    app.details.numeAfectiune = app.details.afectiuneC.get()

    for i in app.details.valori:
        if app.details.numeAfectiune.__contains__(i[1]):
            app.details.inserareIDAfectiune = i[0]

    app.details.inserareGravitate = app.details.inserareGravitate.replace(' ', '').replace('\n', '')
    app.details.inserareRataVindecare = app.details.inserareRataVindecare.replace(' ', '').replace('\n', '')
    app.details.inserareTratament = app.details.inserareTratament.replace(' ', '').replace('\n', '')
    app.details.inserareRataSuicid = app.details.inserareRataSuicid.replace(' ', '').replace('\n', '')
    app.details.inserareSpecificitateGen = app.details.inserareSpecificitateGen.replace(' ', '').replace('\n', '')
    app.details.inserareAsistentaPersonala = app.details.inserareAsistentaPersonala.replace(' ', '').replace('\n', '')

    app.cursor.execute(
        'update detalii_afectiune set gravitate = \'' + app.details.inserareGravitate + '\' where afectiune_id = ' + str(
            app.details.inserareIDAfectiune))
    app.cursor.execute(
        'update detalii_afectiune set rata_vindecare = \'' + app.details.inserareRataVindecare + '\' where afectiune_id = ' + str(
            app.details.inserareIDAfectiune))
    app.cursor.execute(
        'update detalii_afectiune set tratament_medicamentos = \'' + app.details.inserareTratament + '\' where afectiune_id = ' + str(
            app.details.inserareIDAfectiune))
    app.cursor.execute(
        'update detalii_afectiune set rata_suicid = \'' + app.details.inserareRataSuicid + '\' where afectiune_id = ' + str(
            app.details.inserareIDAfectiune))
    app.cursor.execute(
        'update detalii_afectiune set specificitate_gen = \'' + app.details.inserareSpecificitateGen + '\' where afectiune_id = ' + str(
            app.details.inserareIDAfectiune))
    app.cursor.execute(
        'update detalii_afectiune set asistenta_personala = \'' + app.details.inserareAsistentaPersonala + '\' where afectiune_id = ' + str(
            app.details.inserareIDAfectiune))
    app.cursor.execute('commit')

    app.details.gravitateT.delete("1.0", "end")
    app.details.vindecareT.delete("1.0", "end")
    app.details.tratamentC.set('')
    app.details.suicidT.delete("1.0", "end")
    app.details.specificitateC.set('')
    app.details.asistentaPersonalaC.set('')
    app.details.afectiuneC.set('')

    open_popup('Datele au fost modificate')


def adaugaDetalii():
    app.details.inserareGravitate = app.details.gravitateT.get("1.0", "end")
    app.details.inserareRataVindecare = app.details.vindecareT.get("1.0", "end")
    app.details.inserareTratament = app.details.tratamentC.get()
    app.details.inserareRataSuicid = app.details.suicidT.get("1.0", "end")
    app.details.inserareSpecificitateGen = app.details.specificitateC.get()
    app.details.inserareAsistentaPersonala = app.details.asistentaPersonalaC.get()
    app.details.numeAfectiune = app.details.afectiuneC.get()

    for i in app.details.valori:
        if app.details.numeAfectiune.__contains__(i[1]):
            app.details.inserareIDAfectiune = i[0]

    app.details.inserareGravitate = app.details.inserareGravitate.replace(' ', '').replace('\n', '')
    app.details.inserareRataVindecare = app.details.inserareRataVindecare.replace(' ', '').replace('\n', '')
    app.details.inserareTratament = app.details.inserareTratament.replace(' ', '').replace('\n', '')
    app.details.inserareRataSuicid = app.details.inserareRataSuicid.replace(' ', '').replace('\n', '')
    app.details.inserareSpecificitateGen = app.details.inserareSpecificitateGen.replace(' ', '').replace('\n', '')
    app.details.inserareAsistentaPersonala = app.details.inserareAsistentaPersonala.replace(' ', '').replace('\n', '')

    try:
        app.cursor.execute(
            'insert into detalii_afectiune values(' + app.details.inserareGravitate + ',' + app.details.inserareRataVindecare + ',\'' + app.details.inserareTratament + '\',' + app.details.inserareRataSuicid + ',\'' + app.details.inserareSpecificitateGen + '\',\'' + app.details.inserareAsistentaPersonala + '\',' + str(
                app.details.inserareIDAfectiune) + ')')
        open_popup("Datele au fost intorduse")
        app.cursor.execute('commit')
        app.details.gravitateT.delete("1.0", "end")
        app.details.vindecareT.delete("1.0", "end")
        app.details.tratamentC.set('')
        app.details.suicidT.delete("1.0", "end")
        app.details.specificitateC.set('')
        app.details.asistentaPersonalaC.set('')
        app.details.afectiuneC.set('')
    except Exception as e:
        if str(e).upper().__contains__('DETALII_AFECTIUNE__IDX'.upper()):
            open_popupEror("Exista deja detalii pentru aceasta afectiune!\n")
        if str(e).upper().__contains__('ORA-00936'.upper()):
            open_popupEror("Completati toate campurile obligatorii!\n")
        elif str(e).upper().__contains__('ORA-01438'.upper()):
            open_popupEror('Valoarea unui camp este prea mare!')
        elif str(e).upper().__contains__('DETALII_RATA_VINDECARE_CK'.upper()):
            open_popupEror('Rata de vindecare nu este valida!')
        elif str(e).upper().__contains__('DETALII_RATA_SUICID_CK'.upper()):
            open_popupEror('Rata de suicid nu este valida!')
        elif str(e).upper().__contains__('ORA-00917'.upper()):
            open_popupEror('O valoare contine caractere ilegale!')
        print(e)
        traceback.print_exc()


class PageDrugs:

    def __init__(self, app):
        self.app = app
        image2 = Image.open("img1.png")
        test1 = ImageTk.PhotoImage(image2)
        self.imgBrain = tk.Label(image=test1, borderwidth=0, background="gray5")
        self.imgBrain.image = test1
        self.medicamenteL = tk.Label(text="Medicamente", borderwidth=0, bg="gray5", font=self.app.helv30,
                                     fg="LightBlue")
        self.adaugareL = tk.Label(text="Adaugare medicament", borderwidth=0, bg="gray5", font=self.app.helv16,
                                  fg="LightBlue")
        self.vizualizareL = tk.Label(text="Vizualizare medicamente", borderwidth=0, bg="gray5", font=self.app.helv16,
                                     fg="LightBlue")
        self.modificareL = tk.Label(text="Selectati medicamentul si alegeti noile date", borderwidth=0, bg="gray5",
                                    font=self.app.helv16, fg="LightBlue")
        self.stergereL = tk.Label(text="Stergere medicament", borderwidth=0, bg="gray5", font=self.app.helv16,
                                  fg="LightBlue")
        self.numeT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.numeL = tk.Label(text="Nume", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.firmaT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.firmaL = tk.Label(text="Firma producatoare", borderwidth=0, bg="gray5", font=self.app.helv14,
                               fg="LightBlue")

        self.adaugaB = tk.Button(master=self.app, text="Adauga medicament", width=15, height=1, bg="blue",
                                 fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=0,
                                 command=adaugaMedicament)
        self.strOperatie = tk.StringVar()
        self.operatieC = tk.ttk.Combobox(self.app, width=int(self.app.width / 80), textvariable=self.strOperatie)
        self.operatieC['values'] = (
            ' Vizualizare', ' Adaugare', ' Modificare', ' Stergere')  # se vor citi din baza de date
        self.operatieC.current(1)
        self.operatieC.bind("<<ComboboxSelected>>", selectOperatieMedicament)
        self.operatieC['state'] = 'readonly'

        self.inserareNume = None
        self.inserareFirma = None
        self.table = []
        self.page = 0
        self.nextB = tk.Button(master=self.app, text="Urmatoarele", width=15, height=1, bg="blue", fg="LightBlue",
                               font=self.app.helv14, background="grey10", borderwidth=1, command=nextTableMedicamente)
        self.stergereB = tk.Button(master=self.app, text="Stergere", width=15, height=1, bg="blue", fg="LightBlue",
                                   font=self.app.helv14, background="grey10", borderwidth=0, command=stergereMedicament)

        self.prevB = tk.Button(master=self.app, text="Anterioarele", width=15, height=1, bg="blue", fg="LightBlue",
                               font=self.app.helv14, background="grey10", borderwidth=1, command=prevTableMedicamente)
        self.modificaB = tk.Button(master=self.app, text="Modifica medicamentul", width=25, height=1, bg="blue",
                                   fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=1,
                                   command=modificaMedicament)
        self.strstergere = tk.StringVar()
        self.stergereC = tk.ttk.Combobox(self.app, width=int(self.app.width / 40), textvariable=self.strstergere)

        self.stergereC['state'] = 'readonly'

        self.valori = []
        app.cursor.execute('select nume,firma_producatoare from medicament')
        rows = app.cursor.fetchall()

        for i in rows:
            try:
                self.valori.append(i[0] + ' : ' + i[1])
            except TypeError:
                self.valori.append(i[0])

        self.stergereC['values'] = self.valori  # se vor citi din baza de date

    def ShowDrugs(self, tip):
        self.medicamenteL.place(relx=0.5, rely=0.15, anchor='center')
        self.imgBrain.place(relx=0.8, rely=0.6, anchor='nw')
        self.operatieC.place(relx=0.45, rely=0.2, anchor='w')
        if tip == 1:
            self.adaugareL.place(relx=0.05, rely=0.3, anchor='w')
            self.numeT.place(relx=0.15, rely=0.40, anchor='w')
            self.numeL.place(relx=0.05, rely=0.40, anchor='w')
            self.firmaT.place(relx=0.15, rely=0.45, anchor='w')
            self.firmaL.place(relx=0.05, rely=0.45, anchor='w')
            self.adaugaB.place(relx=0.2, rely=0.55, anchor='w')
        elif tip == 0:
            self.vizualizareL.place(relx=0.05, rely=0.3, anchor='w')
            self.nextB.place(relx=0.5, rely=0.95, anchor='center')
            self.prevB.place(relx=0.2, rely=0.95, anchor='center')
        elif tip == 2:
            self.modificareL.place(relx=0.05, rely=0.3, anchor='w')
            self.stergereC.place(relx=0.35, rely=0.3, anchor='w')
            self.numeT.place(relx=0.15, rely=0.40, anchor='w')
            self.numeL.place(relx=0.05, rely=0.40, anchor='w')
            self.firmaT.place(relx=0.15, rely=0.45, anchor='w')
            self.firmaL.place(relx=0.05, rely=0.45, anchor='w')
            self.modificaB.place(relx=0.2, rely=0.55, anchor='w')
        elif tip == 3:
            self.stergereL.place(relx=0.05, rely=0.3, anchor='w')
            self.stergereC.place(relx=0.05, rely=0.4, anchor='w')
            self.stergereB.place(relx=0.25, rely=0.4, anchor='w')

    def HideDrugs(self):
        self.modificaB.place_forget()
        self.stergereB.place_forget()
        self.stergereC.place_forget()
        self.medicamenteL.place_forget()
        self.imgBrain.place_forget()
        self.operatieC.place_forget()
        self.adaugareL.place_forget()
        self.numeT.place_forget()
        self.numeL.place_forget()
        self.firmaT.place_forget()
        self.firmaL.place_forget()
        self.adaugaB.place_forget()
        self.vizualizareL.place_forget()
        self.modificareL.place_forget()
        self.stergereL.place_forget()
        self.nextB.place_forget()
        self.prevB.place_forget()
        for i in self.table:
            i.place_forget()


class PageHome:
    def __init__(self, app):
        self.app = app
        image = Image.open("img.png")
        test = ImageTk.PhotoImage(image)
        self.imgAcasa = tk.Label(image=test, borderwidth=0)
        self.imgAcasa.image = test

    def ShowHome(self):
        self.imgAcasa.place(relx=0.35, rely=0.3, anchor='nw')

    def HideHome(self):
        self.imgAcasa.place_forget()


class PagePatient:
    def __init__(self, app):
        image = Image.open("img1.png")
        test = ImageTk.PhotoImage(image)
        self.imgBrain = tk.Label(image=test, borderwidth=0, background="gray5")
        self.imgBrain.image = test
        self.app = app
        self.page = 0
        self.adaugareL = tk.Label(text="Adaugare pacient", borderwidth=0, bg="gray5", font=self.app.helv16,
                                  fg="LightBlue")
        self.vizualizareL = tk.Label(text="Vizualizare pacienti", borderwidth=0, bg="gray5", font=self.app.helv16,
                                     fg="LightBlue")
        self.modificareL = tk.Label(text="Selectati un paciet si modificati datele actuale", borderwidth=0, bg="gray5",
                                    font=self.app.helv16, fg="LightBlue")
        self.stergereL = tk.Label(text="Stergere pacient", borderwidth=0, bg="gray5", font=self.app.helv16,
                                  fg="LightBlue")
        self.pacientiL = tk.Label(text="Pacienti", borderwidth=0, bg="gray5", font=self.app.helv30, fg="LightBlue")

        self.numeT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.numeL = tk.Label(text="Nume*", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.prenumeT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.prenumeL = tk.Label(text="Prenume*", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.dataNasteriiT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.dataNasteriiL = tk.Label(text="Data nasterii* ", borderwidth=0, bg="gray5", font=self.app.helv14,
                                      fg="LightBlue")
        self.telefonT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.telefonL = tk.Label(text="Numar de telefon", borderwidth=0, bg="gray5", font=self.app.helv14,
                                 fg="LightBlue")
        self.genL = tk.Label(text="Gen*", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.strGen = tk.StringVar()
        self.genC = tk.ttk.Combobox(self.app, width=int(self.app.width / 31), textvariable=self.strGen)
        self.genC['values'] = (' Masculin', ' Feminin',)  # se vor citi din baza de date
        self.adresaT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.adresaL = tk.Label(text="Adresa", borderwidth=0, bg="gray5", font=app.helv14, fg="LightBlue")
        self.adaugaB = tk.Button(master=self.app, text="Adauga", width=15, height=1, bg="blue", fg="LightBlue",
                                 font=self.app.helv14, background="grey10", borderwidth=1, command=adaugaPacient)

        self.nextB = tk.Button(master=self.app, text="Urmatori pacienti", width=15, height=1, bg="blue", fg="LightBlue",
                               font=self.app.helv14, background="grey10", borderwidth=1, command=nextTablePacienti)
        self.stergeB = tk.Button(master=self.app, text="Sterge", width=15, height=1, bg="blue", fg="LightBlue",
                                 font=self.app.helv14, background="grey10", borderwidth=1, command=stergerePacient)

        self.modificaB = tk.Button(master=self.app, text="Modifica", width=15, height=1, bg="blue", fg="LightBlue",
                                   font=self.app.helv14, background="grey10", borderwidth=1, command=modificaPacient)

        self.prevB = tk.Button(master=self.app, text="Pacienti anteriori", width=15, height=1, bg="blue",
                               fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=1,
                               command=prevTablePacienti)

        self.afisareActualB = tk.Button(master=self.app, text="Afisati valorile actuale", width=25, height=1, bg="blue",
                                        fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=1,
                                        command=seteazaValoriActualePacient)
        self.stergereL1 = tk.Label(text="Alegeti pacientul de sters!", borderwidth=0, bg="gray5", font=app.helv14,
                                   fg="LightBlue")
        self.strPacient = tk.StringVar()
        self.pacientC = tk.ttk.Combobox(app, width=int(app.width / 32), textvariable=self.strPacient)

        app.cursor.execute('select nume , prenume,NUMAR_TELEFON  from pacient order by id asc')
        rows = app.cursor.fetchall()
        self.pacienti = []
        pacienti1 = []

        for i in rows:
            self.pacienti.append(i[2])
            pacienti1.append((i[0] + ' ' + i[1] + ' ' + i[2]))

        self.pacientC['state'] = 'readonly'
        self.pacientC['values'] = pacienti1

        self.strOperatie = tk.StringVar()
        self.operatieC = tk.ttk.Combobox(self.app, width=int(app.width / 80), textvariable=self.strOperatie)
        self.operatieC['values'] = (
            ' Vizualizare', ' Adaugare', ' Modificare', ' Stergere')  # se vor citi din baza de date
        self.operatieC.current(1)
        self.operatieC.bind("<<ComboboxSelected>>", selectOperatiePacient)
        self.operatieC['state'] = 'readonly'
        self.table = []
        self.inserareNume = None
        self.inserarePrenume = None
        self.inserareDataNastere = None
        self.inserareTelefon = None
        self.inserareGen = None
        self.inserareAdresa = None

    def ShowPatient(self, tip):
        self.pacientiL.place(relx=0.5, rely=0.15, anchor='center')
        self.operatieC.place(relx=0.5, rely=0.20, anchor='center')
        if tip == 1:
            self.imgBrain.place(relx=0.8, rely=0.6, anchor='nw')
            self.adaugareL.place(relx=0.05, rely=0.25, anchor='w')
            self.numeT.place(relx=0.15, rely=0.3, anchor='w')
            self.numeL.place(relx=0.05, rely=0.3, anchor='w')
            self.prenumeT.place(relx=0.15, rely=0.35, anchor='w')
            self.prenumeL.place(relx=0.05, rely=0.35, anchor='w')
            self.dataNasteriiT.place(relx=0.15, rely=0.40, anchor='w')
            self.dataNasteriiL.place(relx=0.05, rely=0.40, anchor='w')
            self.telefonT.place(relx=0.15, rely=0.45, anchor='w')
            self.telefonL.place(relx=0.05, rely=0.45, anchor='w')
            self.genL.place(relx=0.05, rely=0.50, anchor='w')
            self.genC.place(relx=0.15, rely=0.50, anchor='w')
            self.adresaT.place(relx=0.15, rely=0.55, anchor='w')
            self.adresaL.place(relx=0.05, rely=0.55, anchor='w')
            self.adaugaB.place(relx=0.25, rely=0.65, anchor='center')
        elif tip == 0:
            self.vizualizareL.place(relx=0.05, rely=0.25, anchor='w')
            self.nextB.place(relx=0.6, rely=0.95, anchor='center')
            self.prevB.place(relx=0.4, rely=0.95, anchor='center')
        elif tip == 2:
            self.modificareL.place(relx=0.05, rely=0.25, anchor='w')
            self.pacientC.place(relx=0.35, rely=0.25, anchor='w')
            self.afisareActualB.place(relx=0.60, rely=0.25, anchor='w')
            self.imgBrain.place(relx=0.8, rely=0.6, anchor='nw')
            self.numeT.place(relx=0.15, rely=0.3, anchor='w')
            self.numeL.place(relx=0.05, rely=0.3, anchor='w')
            self.prenumeT.place(relx=0.15, rely=0.35, anchor='w')
            self.prenumeL.place(relx=0.05, rely=0.35, anchor='w')
            self.dataNasteriiT.place(relx=0.15, rely=0.40, anchor='w')
            self.dataNasteriiL.place(relx=0.05, rely=0.40, anchor='w')
            self.telefonT.place(relx=0.15, rely=0.45, anchor='w')
            self.telefonL.place(relx=0.05, rely=0.45, anchor='w')
            self.genL.place(relx=0.05, rely=0.50, anchor='w')
            self.genC.place(relx=0.15, rely=0.50, anchor='w')
            self.adresaT.place(relx=0.15, rely=0.55, anchor='w')
            self.adresaL.place(relx=0.05, rely=0.55, anchor='w')
            self.modificaB.place(relx=0.25, rely=0.65, anchor='center')

        elif tip == 3:
            self.stergereL.place(relx=0.05, rely=0.25, anchor='w')
            self.stergereL1.place(relx=0.05, rely=0.35, anchor='w')
            self.pacientC.place(relx=0.25, rely=0.35, anchor='w')
            self.stergeB.place(relx=0.50, rely=0.35, anchor='w')

    def HidePatient(self):
        self.afisareActualB.place_forget()
        self.modificaB.place_forget()
        self.stergeB.place_forget()
        self.operatieC.place_forget()
        self.adaugaB.place_forget()
        self.vizualizareL.place_forget()
        self.modificareL.place_forget()
        self.stergereL.place_forget()
        self.adaugareL.place_forget()
        self.pacientiL.place_forget()
        self.imgBrain.place_forget()
        self.numeT.place_forget()
        self.numeL.place_forget()
        self.prenumeT.place_forget()
        self.prenumeL.place_forget()
        self.dataNasteriiT.place_forget()
        self.dataNasteriiL.place_forget()
        self.telefonT.place_forget()
        self.telefonL.place_forget()
        self.genL.place_forget()
        self.genC.place_forget()
        self.adresaT.place_forget()
        self.adresaL.place_forget()
        self.adaugaB.place_forget()
        self.nextB.place_forget()
        self.stergereL1.place_forget()
        self.pacientC.place_forget()
        self.prevB.place_forget()
        for i in self.table:
            i.place_forget()


class PageSheet:

    def __init__(self, app):
        self.intrareOrar = None
        image2 = Image.open("img1.png")
        test1 = ImageTk.PhotoImage(image2)
        self.imgBrain = tk.Label(image=test1, borderwidth=0, background="gray5")
        self.imgBrain.image = test1
        self.app = app
        self.fiseL = tk.Label(text="Fise medicale", borderwidth=0, bg="gray5", font=self.app.helv30, fg="LightBlue")
        self.adaugareL = tk.Label(text="Adaugare fisa noua", borderwidth=0, bg="gray5", font=self.app.helv16,
                                  fg="LightBlue")
        self.vizualizareL = tk.Label(text="Vizualizare inregistrari", borderwidth=0, bg="gray5", font=self.app.helv16,
                                     fg="LightBlue")
        self.modificareL = tk.Label(text="Selectati ID-ul fisei de modificat", borderwidth=0, bg="gray5",
                                    font=self.app.helv16, fg="LightBlue")
        self.stergereL = tk.Label(text="Stergere fisa dupa ID", borderwidth=0, bg="gray5", font=app.helv16,
                                  fg="LightBlue")
        self.dataEliberareT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.pacientL = tk.Label(text="Pacient*", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.medicamenteL = tk.Label(text="Medicamente", borderwidth=0, bg="gray5", font=self.app.helv14,
                                     fg="LightBlue")
        self.afectiuniL = tk.Label(text="Afectiuni", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.medicL = tk.Label(text="Medic psihiatru*", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.dataEliberareL = tk.Label(text="Data eliberare fisa*", borderwidth=0, bg="gray5", font=self.app.helv14,
                                       fg="LightBlue")
        self.revenireControlT = tk.Text(self.app, height=1, width=int(app.width / 40))
        self.revenireControlL = tk.Label(text="Data revenire control*", borderwidth=0, bg="gray5", font=self.app.helv14,
                                         fg="LightBlue")
        self.controlAnteriorT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.controlAnteriorL = tk.Label(text="Data control anterior", borderwidth=0, bg="gray5", font=self.app.helv14,
                                         fg="LightBlue")

        self.strOperatie = tk.StringVar()
        self.operatieC = tk.ttk.Combobox(self.app, width=int(self.app.width / 80), textvariable=self.strOperatie)
        self.operatieC['values'] = (
            ' Vizualizare', ' Adaugare', ' Modificare', ' Stergere')  # se vor citi din baza de date
        self.operatieC.current(1)
        self.operatieC.bind("<<ComboboxSelected>>", selectOperatieFise)
        self.operatieC['state'] = 'readonly'

        self.strPacient = tk.StringVar()
        self.pacientC = tk.ttk.Combobox(app, width=int(app.width / 32), textvariable=self.strPacient)

        app.cursor.execute('select   nume , prenume ,numar_telefon from pacient order by id asc')
        rows = app.cursor.fetchall()
        self.pacienti = []
        pacienti1 = []

        for i in rows:
            self.pacienti.append(i[2])
            pacienti1.append((i[0] + ' ' + i[1] + ' ' + i[2]))

        self.pacientC['state'] = 'readonly'
        self.pacientC['values'] = pacienti1

        app.cursor.execute('select id , nume , prenume,numar_telefon  from psihiatru order by id asc')
        rows = app.cursor.fetchall()
        self.psihiatri = []
        psihiatri1 = []

        for i in rows:
            self.psihiatri.append((i[0], i[1] + ' ' + i[2] + ' ' + i[3]))
            psihiatri1.append((i[1] + ' ' + i[2] + ' ' + i[3]))

        self.strMedic = tk.StringVar()
        self.medicC = tk.ttk.Combobox(self.app, width=int(self.app.width / 32), textvariable=self.strMedic)
        self.medicC['state'] = 'readonly'
        self.medicC['values'] = psihiatri1
        self.medicamenteLB = tk.Listbox(self.app, selectmode=tk.MULTIPLE, height=5, width=int(self.app.width / 30),
                                        exportselection=False)

        app.cursor.execute('select * from medicament order by ID')
        rows = app.cursor.fetchall()

        self.valuesMed = []

        for i in rows:
            try:
                self.valuesMed.append((i[0], i[1] + ' de la :' + i[2]))
            except:
                self.valuesMed.append((i[0], i[1]))

        for val in self.valuesMed:
            self.medicamenteLB.insert(tk.END, val[1])

        self.afectiuniLB = tk.Listbox(self.app, selectmode=tk.MULTIPLE, height=5, width=int(self.app.width / 30),
                                      exportselection=False)

        app.cursor.execute('select * from afectiune order by ID')
        rows = app.cursor.fetchall()

        self.valuesAf = []

        for i in rows:
            self.valuesAf.append((i[0], i[1]))

        for val in self.valuesAf:
            self.afectiuniLB.insert(tk.END, val[1])

        self.adaugaB = tk.Button(master=self.app, text="Adauga", width=15, height=1, bg="blue", fg="LightBlue",
                                 font=self.app.helv14, background="grey10", borderwidth=1, command=adaugaFisa)

        self.nextB = tk.Button(master=self.app, text="Pagina urmatoare", width=15, height=1, bg="blue", fg="LightBlue",
                               font=self.app.helv14, background="grey10", borderwidth=1, command=nextTableFisa)
        self.stergereB = tk.Button(master=self.app, text="Sterge fisa", width=15, height=1, bg="blue", fg="LightBlue",
                                   font=self.app.helv14, background="grey10", borderwidth=1, command=stergereFisa)

        self.modificareB = tk.Button(master=self.app, text="Modifica fisa", width=15, height=1, bg="blue",
                                     fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=1,
                                     command=modificaFisa)
        self.modificareNumeB = tk.Button(master=self.app, text="Schimbati numele pe fisa", width=30, height=1,
                                         bg="blue", fg="LightBlue", font=self.app.helv14, background="grey10",
                                         borderwidth=1, command=schimbareNumePacientFisa)
        self.modificarePsihiatruB = tk.Button(master=self.app, text="Schimbati numele pe fisa", width=30, height=1,
                                              bg="blue", fg="LightBlue", font=self.app.helv14, background="grey10",
                                              borderwidth=1, command=schimbareNumePsihiatruFisa)

        self.prevB = tk.Button(master=self.app, text="Pagina anterioara", width=15, height=1, bg="blue", fg="LightBlue",
                               font=self.app.helv14, background="grey10", borderwidth=1, command=prevTableFisa)

        self.detaliiT = tk.Text(self.app, height=3, width=int(self.app.width / 40))
        self.detaliiL = tk.Label(text="Mod administrare", borderwidth=0, bg="gray5", font=self.app.helv14,
                                 fg="LightBlue")

        self.cantitateT = tk.Text(self.app, height=3, width=int(self.app.width / 40))
        self.cantitateL = tk.Label(text="Cantitate", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.stergereL1 = tk.Label(text="Selectati ID-ul fisei de sters", borderwidth=0, bg="gray5",
                                   font=self.app.helv14, fg="LightBlue")
        self.msgL = tk.Label(
            text="Se vor introduce in ordine pentru fiecare medicament pe o linie cantitatea si programul.",
            borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")

        self.strstergere = tk.StringVar()
        self.stergereC = tk.ttk.Combobox(self.app, width=int(self.app.width / 40), textvariable=self.strstergere)

        self.stergereC['state'] = 'readonly'

        self.valori = []
        app.cursor.execute('select id from fisa_medicala')
        rows = app.cursor.fetchall()

        for i in rows:
            self.valori.append(i[0])

        self.stergereC['values'] = self.valori  # se vor citi din baza de date

        #
        self.modificare = tk.StringVar()
        self.modificareC = tk.ttk.Combobox(self.app, width=int(self.app.width / 40), textvariable=self.modificare)

        self.modificareC['state'] = 'readonly'

        self.valori = []
        app.cursor.execute('select id from fisa_medicala')
        rows = app.cursor.fetchall()
        for i in rows:
            self.valori.append(i[0])
        self.modificareC['values'] = self.valori  # se vor citi din baza de date

        self.modificareL1 = tk.Label(text="Alegeti noile valori.", borderwidth=0, bg="gray5", font=self.app.helv16,
                                     fg="LightBlue")
        self.modificaFullB = tk.Button(master=self.app, text="Modifica pacientul", width=15, height=1, bg="blue",
                                       fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=1,
                                       command=modificaFisaFull)
        self.modificareL2 = tk.Label(text="Alegeti atributul de modificat.", borderwidth=0, bg="gray5",
                                     font=self.app.helv30, fg="LightBlue")
        self.modificareNume = tk.Label(text="Alegeti noul nume al pacientului caruia ii apartine fisa", borderwidth=0,
                                       bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.modificarePsihiatru = tk.Label(text="Alegeti noul nume al psihiatrului", borderwidth=0, bg="gray5",
                                            font=self.app.helv14, fg="LightBlue")

        self.modificareDataEliberare = tk.Label(text="Alegeti noua data de eliberare", borderwidth=0, bg="gray5",
                                                font=self.app.helv14, fg="LightBlue")
        self.modificareDataRevenire = tk.Label(text="Alegeti noua data de revenire", borderwidth=0, bg="gray5",
                                               font=self.app.helv14, fg="LightBlue")

        self.modificareAtributFisa = tk.StringVar()
        self.valoriModificat = []
        rows = ['Pacient', 'Medic psihiatru', 'Data eliberare fisa', 'Data revenire control', 'Medicamente',
                'Afectiuni']
        self.modificareAtribut = tk.ttk.Combobox(self.app, width=int(self.app.width / 40),
                                                 textvariable=self.modificareAtributFisa)
        self.modificareAtribut.bind("<<ComboboxSelected>>", selectAtributDeModificatFisa)
        for i in rows:
            self.valoriModificat.append(i)

        self.modificareAtribut['values'] = self.valoriModificat  # se vor citi din baza de date
        self.modificareDataEliberareB = tk.Button(master=self.app, text="Modifica data", width=15, height=1, bg="blue",
                                                  fg="LightBlue", font=self.app.helv14, background="grey10",
                                                  borderwidth=1, command=schimbaDataEliberareFisa)
        self.modficareDataEliberareT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.modificareDataRevenireB = tk.Button(master=self.app, text="Modifica data", width=15, height=1, bg="blue",
                                                 fg="LightBlue", font=self.app.helv14, background="grey10",
                                                 borderwidth=1, command=schimbaDataRevenireFisa)
        self.modificareMedicamenteB = tk.Button(master=self.app, text="Modifica medicament", width=25, height=1,
                                                bg="blue", fg="LightBlue", font=self.app.helv14, background="grey10",
                                                borderwidth=1, command=schimbaMedicamenteFisa)
        self.modificareAfectiuniB = tk.Button(master=self.app, text="Modifica afectiunile", width=25, height=1,
                                              bg="blue", fg="LightBlue", font=self.app.helv14, background="grey10",
                                              borderwidth=1, command=schimbaAfectiuniFisa)
        self.modficareDataRevenireT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        # datele introduse
        self.intrarePacient = None
        self.intrareMedic = None
        self.intrareDataEliberare = None
        self.intrareControlUrmator = None
        self.intrareMedicamente = None
        self.intrareAfectiuni = None
        self.table = []
        self.page = 0
        self.intrareIDPacient = None
        self.intrareIDMedic = None
        self.idFisaDeModificat = None

    def ShowSheet(self, tip):  # 0-vizualizare 1-adaugare 2-modificare 3-seterge
        self.operatieC.place(relx=0.5, rely=0.20, anchor='center')
        self.fiseL.place(relx=0.5, rely=0.15, anchor='center')

        if tip == 1:
            self.adaugareL.place(relx=0.05, rely=0.25, anchor='w')
            self.pacientC.place(relx=0.15, rely=0.3, anchor='w')
            self.pacientL.place(relx=0.05, rely=0.3, anchor='w')
            self.medicC.place(relx=0.15, rely=0.35, anchor='w')
            self.medicL.place(relx=0.05, rely=0.35, anchor='w')
            self.dataEliberareT.place(relx=0.15, rely=0.4, anchor='w')
            self.dataEliberareL.place(relx=0.05, rely=0.4, anchor='w')
            self.revenireControlT.place(relx=0.15, rely=0.45, anchor='w')
            self.revenireControlL.place(relx=0.05, rely=0.45, anchor='w')
            self.medicamenteLB.place(relx=0.15, rely=0.53, anchor='w')
            self.medicamenteL.place(relx=0.05, rely=0.55, anchor='w')
            self.afectiuniLB.place(relx=0.15, rely=0.65, anchor='w')
            self.afectiuniL.place(relx=0.05, rely=0.62, anchor='w')
            self.adaugaB.place(relx=0.5, rely=0.50, anchor='n')
            self.msgL.place(relx=0.05, rely=0.75, anchor='w')
            self.cantitateT.place(relx=0.15, rely=0.80, anchor='w')
            self.cantitateL.place(relx=0.05, rely=0.80, anchor='w')
            self.detaliiT.place(relx=0.15, rely=0.90, anchor='w')
            self.detaliiL.place(relx=0.05, rely=0.90, anchor='w')
            self.imgBrain.place(relx=0.8, rely=0.6, anchor='nw')
        elif tip == 0:
            self.vizualizareL.place(relx=0.05, rely=0.25, anchor='w')
            self.nextB.place(relx=0.6, rely=0.95, anchor='center')
            self.prevB.place(relx=0.4, rely=0.95, anchor='center')
        elif tip == 2:
            self.modificareL.place(relx=0.05, rely=0.25, anchor='w')
            self.imgBrain.place(relx=0.8, rely=0.6, anchor='nw')
            self.modificareC.place(relx=0.05, rely=0.3, anchor='w')
            self.modificareB.place(relx=0.1, rely=0.6, anchor='w')
        elif tip == 3:
            self.stergereL.place(relx=0.05, rely=0.25, anchor='w')
            self.imgBrain.place(relx=0.8, rely=0.6, anchor='nw')
            self.stergereC.place(relx=0.25, rely=0.35, anchor='w')
            self.stergereL1.place(relx=0.05, rely=0.35, anchor='w')
            self.stergereB.place(relx=0.45, rely=0.35, anchor='w')
        elif tip == 4:
            self.modificareL2.place(relx=0.05, rely=0.25, anchor='w')
            self.modificareAtribut.place(relx=0.25, rely=0.35, anchor='w')
        elif tip == 5:
            self.modificareNume.place(relx=0.05, rely=0.35, anchor='w')
            self.modificareNumeB.place(relx=0.55, rely=0.45, anchor='w')
            self.pacientC.place(relx=0.15, rely=0.45, anchor='w')
        elif tip == 6:
            self.modificarePsihiatru.place(relx=0.05, rely=0.35, anchor='w')
            self.modificarePsihiatruB.place(relx=0.55, rely=0.45, anchor='w')
            self.medicC.place(relx=0.15, rely=0.45, anchor='w')
        elif tip == 7:
            self.modificareDataEliberare.place(relx=0.05, rely=0.35, anchor='w')
            self.modificareDataEliberareB.place(relx=0.55, rely=0.45, anchor='w')
            self.modficareDataEliberareT.place(relx=0.15, rely=0.45, anchor='w')
        elif tip == 8:
            self.modificareDataRevenire.place(relx=0.05, rely=0.35, anchor='w')
            self.modificareDataRevenireB.place(relx=0.55, rely=0.45, anchor='w')
            self.modficareDataRevenireT.place(relx=0.15, rely=0.45, anchor='w')
        elif tip == 9:
            self.medicamenteLB.place(relx=0.15, rely=0.45, anchor='w')
            self.medicamenteL.place(relx=0.05, rely=0.45, anchor='w')
            self.modificareMedicamenteB.place(relx=0.55, rely=0.55, anchor='w')
            self.cantitateT.place(relx=0.15, rely=0.55, anchor='w')
            self.cantitateL.place(relx=0.05, rely=0.55, anchor='w')
            self.detaliiT.place(relx=0.15, rely=0.65, anchor='w')
            self.detaliiL.place(relx=0.05, rely=0.65, anchor='w')
        elif tip == 10:
            self.afectiuniLB.place(relx=0.15, rely=0.35, anchor='w')
            self.afectiuniL.place(relx=0.05, rely=0.35, anchor='w')
            self.modificareAfectiuniB.place(relx=0.55, rely=0.55, anchor='w')

    def HideSheet(self):
        self.modificareDataRevenire.place_forget()
        self.modificareDataRevenireB.place_forget()
        self.modficareDataRevenireT.place_forget()
        self.modificareDataEliberare.place_forget()
        self.modificareDataEliberareB.place_forget()
        self.modficareDataEliberareT.place_forget()
        self.modificarePsihiatruB.place_forget()
        self.modificareNumeB.place_forget()
        self.modificarePsihiatru.place_forget()
        self.modificareAfectiuniB.place_forget()
        self.modificareMedicamenteB.place_forget()
        self.modificareDataRevenireB.place_forget()
        self.modificareNumeB.place_forget()
        self.modificareNume.place_forget()
        self.modificareAtribut.place_forget()
        self.modificareL2.place_forget()
        self.modificareL1.place_forget()
        self.stergereL1.place_forget()
        self.stergereL.place_forget()
        self.stergereB.place_forget()
        self.cantitateL.place_forget()
        self.vizualizareL.place_forget()
        self.modificareL.place_forget()
        self.stergereL.place_forget()
        self.operatieC.place_forget()
        self.fiseL.place_forget()
        self.imgBrain.place_forget()
        self.adaugareL.place_forget()
        self.dataEliberareT.place_forget()
        self.pacientL.place_forget()
        self.dataEliberareL.place_forget()
        self.revenireControlT.place_forget()
        self.revenireControlL.place_forget()
        self.pacientC.place_forget()
        self.medicamenteLB.place_forget()
        self.medicamenteL.place_forget()
        self.afectiuniLB.place_forget()
        self.afectiuniL.place_forget()
        self.detaliiT.place_forget()
        self.detaliiL.place_forget()
        self.medicC.place_forget()
        self.medicL.place_forget()
        self.adaugaB.place_forget()
        self.nextB.place_forget()
        self.msgL.place_forget()
        self.stergereC.place_forget()
        self.cantitateL.place_forget()
        self.cantitateT.place_forget()
        self.prevB.place_forget()
        self.modificareC.place_forget()
        self.modificareB.place_forget()
        for i in self.table:
            i.place_forget()


class PageDisease:

    def __init__(self, app):
        image2 = Image.open("img1.png")
        test1 = ImageTk.PhotoImage(image2)
        self.imgBrain = tk.Label(image=test1, borderwidth=0, background="gray5")
        self.imgBrain.image = test1
        self.app = app
        self.afectiuniL = tk.Label(text="Afectiuni", borderwidth=0, bg="gray5", font=self.app.helv30, fg="LightBlue")
        self.adaugareL = tk.Label(text="Adaugare afectiune", borderwidth=0, bg="gray5", font=self.app.helv16,
                                  fg="LightBlue")
        self.vizualizareL = tk.Label(text="Vizualizare afectiuni", borderwidth=0, bg="gray5", font=self.app.helv16,
                                     fg="LightBlue")
        self.modificareL = tk.Label(text="Selectati afectiunea si noul ei nume", borderwidth=0, bg="gray5",
                                    font=self.app.helv16, fg="LightBlue")
        self.stergereL = tk.Label(text="Stergere afectiune", borderwidth=0, bg="gray5", font=self.app.helv16,
                                  fg="LightBlue")

        self.stergere1L = tk.Label(text="Selectati afectiunea de sters", borderwidth=0, bg="gray5",
                                   font=self.app.helv16, fg="LightBlue")

        self.numeT = tk.Text(app, height=1, width=int(self.app.width / 40))
        self.numeL = tk.Label(text="Nume*", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")

        self.adaugaB = tk.Button(master=self.app, text="Adauga afectiune", width=15, height=1, bg="blue",
                                 fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=0,
                                 command=adaugareAfectiune)
        self.strOperatie = tk.StringVar()
        self.operatieC = tk.ttk.Combobox(app, width=int(self.app.width / 40), textvariable=self.strOperatie)
        self.operatieC['values'] = (
            ' Vizualizare', ' Adaugare', ' Modificare', ' Stergere')  # se vor citi din baza de date
        self.operatieC.current(1)
        self.operatieC.bind("<<ComboboxSelected>>", selectOperatieAfectiune)
        self.operatieC['state'] = 'readonly'

        self.nextB = tk.Button(master=self.app, text="Urmatoarele", width=15, height=1, bg="blue", fg="LightBlue",
                               font=self.app.helv14, background="grey10", borderwidth=1, command=nextTableAfectiuni)

        self.prevB = tk.Button(master=self.app, text="Anterioarele", width=15, height=1, bg="blue", fg="LightBlue",
                               font=self.app.helv14, background="grey10", borderwidth=1, command=prevTableAfectiuni)

        self.stergereB = tk.Button(master=self.app, text="Stergere", width=15, height=1, bg="blue", fg="LightBlue",
                                   font=self.app.helv14, background="grey10", borderwidth=1, command=stergereAfectiune)
        self.modificareB = tk.Button(master=self.app, text="Modificare", width=15, height=1, bg="blue", fg="LightBlue",
                                     font=self.app.helv14, background="grey10", borderwidth=1,
                                     command=modificaAfectiune)
        app.cursor.execute('select * from afectiune order by ID')
        rows = app.cursor.fetchall()

        self.valuesAf = []

        for i in rows:
            self.valuesAf.append(str(i[1]))

        self.strstergere = tk.StringVar()
        self.stergereC = tk.ttk.Combobox(app, width=int(self.app.width / 40), textvariable=self.strstergere)
        self.stergereC['values'] = self.valuesAf
        self.stergereC['state'] = 'readonly'

        self.insertNume = None
        self.page = 0
        self.table = []

    def ShowDisease(self, tip):
        self.afectiuniL.place(relx=0.5, rely=0.15, anchor='center')
        self.imgBrain.place(relx=0.8, rely=0.6, anchor='nw')
        self.operatieC.place(relx=0.45, rely=0.2, anchor='w')
        if tip == 1:
            self.adaugareL.place(relx=0.05, rely=0.3, anchor='w')
            self.numeT.place(relx=0.15, rely=0.40, anchor='w')
            self.numeL.place(relx=0.05, rely=0.40, anchor='w')
            self.adaugaB.place(relx=0.2, rely=0.50, anchor='w')
        elif tip == 0:
            self.vizualizareL.place(relx=0.05, rely=0.3, anchor='w')
            self.nextB.place(relx=0.1, rely=0.95, anchor='center')
            self.prevB.place(relx=0.3, rely=0.95, anchor='center')
        elif tip == 2:
            self.stergereC.place(relx=0.35, rely=0.3, anchor='w')
            self.modificareL.place(relx=0.05, rely=0.3, anchor='w')
            self.numeT.place(relx=0.15, rely=0.40, anchor='w')
            self.numeL.place(relx=0.05, rely=0.40, anchor='w')
            self.modificareB.place(relx=0.45, rely=0.40, anchor='w')
        elif tip == 3:
            self.stergereL.place(relx=0.05, rely=0.3, anchor='w')
            self.stergereC.place(relx=0.25, rely=0.40, anchor='w')
            self.stergereB.place(relx=0.45, rely=0.40, anchor='w')
            self.stergere1L.place(relx=0.05, rely=0.40, anchor='w')

    def HideDisease(self):
        self.modificareB.place_forget()
        self.stergere1L.place_forget()
        self.stergereC.place_forget()
        self.stergereB.place_forget()
        self.vizualizareL.place_forget()
        self.modificareL.place_forget()
        self.stergereL.place_forget()
        self.afectiuniL.place_forget()
        self.imgBrain.place_forget()
        self.adaugaB.place_forget()
        self.imgBrain.place_forget()
        self.adaugareL.place_forget()
        self.numeT.place_forget()
        self.numeL.place_forget()
        self.adaugaB.place_forget()
        self.operatieC.place_forget()
        self.nextB.place_forget()
        self.prevB.place_forget()
        for i in self.table:
            i.place_forget()


class PageDoctor:

    def __init__(self, app):
        image2 = Image.open("img1.png")
        test1 = ImageTk.PhotoImage(image2)
        self.imgBrain = tk.Label(image=test1, borderwidth=0, background="gray5")
        self.imgBrain.image = test1
        self.app = app
        self.psihiatruL = tk.Label(text="Medici psihiatrii", borderwidth=0, bg="gray5", font=self.app.helv30,
                                   fg="LightBlue")
        self.adaugareL = tk.Label(text="Adaugare psihiatru", borderwidth=0, bg="gray5", font=self.app.helv16,
                                  fg="LightBlue")
        self.vizualizareL = tk.Label(text="Vizualizare psihiatrii", borderwidth=0, bg="gray5", font=self.app.helv16,
                                     fg="LightBlue")
        self.modificareL = tk.Label(text="Modificare date psihiatru", borderwidth=0, bg="gray5", font=self.app.helv16,
                                    fg="LightBlue")
        self.stergereL = tk.Label(text="Stergere psihiatru", borderwidth=0, bg="gray5", font=self.app.helv16,
                                  fg="LightBlue")

        self.numeT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.numeL = tk.Label(text="Nume*", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.prenumeT = tk.Text(app, height=1, width=int(app.width / 40))
        self.prenumeL = tk.Label(text="Prenume*", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")

        self.telefonT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.telefonL = tk.Label(text="Telefon*", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.emailT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.emailL = tk.Label(text="Email", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.numeStergereL = tk.Label(text="Alegeti in functie nume, prenume si numar de telefon medicul de sters",
                                      borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")

        self.adaugaB = tk.Button(master=self.app, text="Adauga psihiatru", width=15, height=1, bg="blue",
                                 fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=0,
                                 command=adaugaDoctor)
        self.stergereB = tk.Button(master=self.app, text="Sterge", width=15, height=1, bg="blue", fg="LightBlue",
                                   font=self.app.helv14, background="grey10", borderwidth=0, command=stergereDoctor)
        self.strOperatie = tk.StringVar()
        self.operatieC = tk.ttk.Combobox(self.app, width=int(self.app.width / 80), textvariable=self.strOperatie)
        self.operatieC['values'] = (
            ' Vizualizare', ' Adaugare', ' Modificare', ' Stergere')  # se vor citi din baza de date
        self.operatieC.current(1)
        self.operatieC.bind("<<ComboboxSelected>>", selectOperatiePsihiatru)
        self.operatieC['state'] = 'readonly'

        self.nextB = tk.Button(master=self.app, text="Urmatoarele", width=15, height=1, bg="blue", fg="LightBlue",
                               font=self.app.helv14, background="grey10", borderwidth=1, command=nextTableDoctor)

        self.prevB = tk.Button(master=self.app, text="Anterioarele", width=15, height=1, bg="blue", fg="LightBlue",
                               font=self.app.helv14, background="grey10", borderwidth=1, command=prevTableDoctor)
        self.modificaB = tk.Button(master=self.app, text="Modifica", width=15, height=1, bg="blue", fg="LightBlue",
                                   font=self.app.helv14, background="grey10", borderwidth=1, command=ModificareDoctor)

        self.afiseazaDoctorB = tk.Button(master=self.app, text="Afiseaza datele initiale", width=25, height=1,
                                         bg="blue", fg="LightBlue", font=self.app.helv14, background="grey10",
                                         borderwidth=1, command=afiseazaDoctor)

        self.strmedicStergere = tk.StringVar()
        self.stergereC = tk.ttk.Combobox(self.app, width=int(self.app.width / 40), textvariable=self.strmedicStergere)
        self.stergereC['state'] = 'readonly'

        valori1 = []
        self.valori = []
        app.cursor.execute('select  nume,prenume,numar_telefon from psihiatru order by id asc')
        rows = app.cursor.fetchall()

        for i in rows:
            self.valori.append(i[2])
            valori1.append(str(i[0]) + ' ' + str(i[1]) + ' ' + str(i[2]))

        self.stergereC['values'] = valori1  # se vor citi din baza de date

        self.insertNume = None
        self.insertPrenume = None
        self.insertTelefon = None
        self.insertEmail = None
        self.page = 0
        self.table = []

    def ShowDoctor(self, tip):
        self.psihiatruL.place(relx=0.5, rely=0.15, anchor='center')
        self.operatieC.place(relx=0.45, rely=0.2, anchor='w')
        #
        if tip == 1:
            self.adaugareL.place(relx=0.05, rely=0.25, anchor='w')
            self.numeT.place(relx=0.15, rely=0.35, anchor='w')
            self.numeL.place(relx=0.05, rely=0.35, anchor='w')
            self.prenumeT.place(relx=0.15, rely=0.40, anchor='w')
            self.prenumeL.place(relx=0.05, rely=0.40, anchor='w')
            self.adaugaB.place(relx=0.25, rely=0.6, anchor='w')
            self.telefonT.place(relx=0.15, rely=0.45, anchor='w')
            self.telefonL.place(relx=0.05, rely=0.45, anchor='w')
            self.emailT.place(relx=0.15, rely=0.50, anchor='w')
            self.emailL.place(relx=0.05, rely=0.50, anchor='w')
            self.imgBrain.place(relx=0.8, rely=0.6, anchor='nw')
        elif tip == 0:
            self.vizualizareL.place(relx=0.05, rely=0.25, anchor='w')
            self.nextB.place(relx=0.6, rely=0.95, anchor='center')
            self.prevB.place(relx=0.4, rely=0.95, anchor='center')
        elif tip == 2:
            self.stergereC.place(relx=0.30, rely=0.25, anchor='w')
            self.afiseazaDoctorB.place(relx=0.55, rely=0.25, anchor='w')
            self.modificareL.place(relx=0.05, rely=0.25, anchor='w')
            self.imgBrain.place(relx=0.8, rely=0.6, anchor='nw')
            self.numeT.place(relx=0.15, rely=0.35, anchor='w')
            self.numeL.place(relx=0.05, rely=0.35, anchor='w')
            self.prenumeT.place(relx=0.15, rely=0.40, anchor='w')
            self.prenumeL.place(relx=0.05, rely=0.40, anchor='w')
            self.telefonT.place(relx=0.15, rely=0.45, anchor='w')
            self.telefonL.place(relx=0.05, rely=0.45, anchor='w')
            self.emailT.place(relx=0.15, rely=0.50, anchor='w')
            self.emailL.place(relx=0.05, rely=0.50, anchor='w')
            self.modificaB.place(relx=0.25, rely=0.6, anchor='w')
        elif tip == 3:
            self.stergereL.place(relx=0.05, rely=0.25, anchor='w')
            self.imgBrain.place(relx=0.8, rely=0.6, anchor='w')
            self.numeStergereL.place(relx=0.05, rely=0.40, anchor='w')
            self.stergereC.place(relx=0.05, rely=0.45, anchor='w')
            self.stergereB.place(relx=0.1, rely=0.65, anchor='w')

    def HideDoctor(self):
        self.afiseazaDoctorB.place_forget()
        self.modificaB.place_forget()
        self.stergereB.place_forget()
        self.vizualizareL.place_forget()
        self.modificareL.place_forget()
        self.stergereL.place_forget()
        self.psihiatruL.place_forget()
        self.imgBrain.place_forget()
        self.adaugareL.place_forget()
        self.numeT.place_forget()
        self.numeL.place_forget()
        self.prenumeT.place_forget()
        self.prenumeL.place_forget()
        self.adaugaB.place_forget()
        self.operatieC.place_forget()
        self.telefonT.place_forget()
        self.telefonL.place_forget()
        self.emailT.place_forget()
        self.emailL.place_forget()
        self.nextB.place_forget()
        self.prevB.place_forget()
        self.numeStergereL.place_forget()
        self.stergereC.place_forget()
        for i in self.table:
            i.place_forget()


class PageDetails:
    def __init__(self, app):
        self.app = app

        # imagine
        image2 = Image.open("img1.png")
        test1 = ImageTk.PhotoImage(image2)
        self.imgBrain = tk.Label(image=test1, borderwidth=0, background="gray5")
        self.imgBrain.image = test1

        # obiecte
        self.lableDetalii = tk.Label(text="Detalii", borderwidth=0, bg="gray5", font=self.app.helv30, fg="LightBlue")
        self.adaugareL = tk.Label(text="Adaugare detalii afectiune", borderwidth=0, bg="gray5", font=self.app.helv16,
                                  fg="LightBlue")
        self.vizualizareL = tk.Label(text="Vizualizare detalii afectiuni", borderwidth=0, bg="gray5",
                                     font=self.app.helv16, fg="LightBlue")
        self.modificareL = tk.Label(text="Alegeti numele afectiuni si noile detalii", borderwidth=0, bg="gray5",
                                    font=self.app.helv16, fg="LightBlue")
        self.stergereL = tk.Label(text="Stergere detalii afectiune", borderwidth=0, bg="gray5", font=self.app.helv16,
                                  fg="LightBlue")

        self.adaugareB = tk.Button(master=self.app, text="Adauga detalii", width=15, height=1, bg="blue",
                                   fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=0,
                                   command=adaugaDetalii)

        self.modificaB = tk.Button(master=self.app, text="Modifica detaliile", width=15, height=1, bg="blue",
                                   fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=0,
                                   command=modificaDetalii)

        self.stergereB = tk.Button(master=self.app, text="Sterge detaliile", width=15, height=1, bg="blue",
                                   fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=0,
                                   command=stergereDetalii)
        self.afisareInitialB = tk.Button(master=self.app, text="Afisare date initiale", width=15, height=1, bg="blue",
                                         fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=0,
                                         command=afisareDetaliiInitiale)
        self.strOperatie = tk.StringVar()
        self.operatieC = tk.ttk.Combobox(app, width=int(self.app.width / 80), textvariable=self.strOperatie)
        self.operatieC['values'] = (
            ' Vizualizare', ' Adaugare', ' Modificare', ' Stergere')  # se vor citi din baza de date
        self.operatieC.current(1)
        self.operatieC.bind("<<ComboboxSelected>>", selectOperatieDetalii)
        self.operatieC['state'] = 'readonly'
        self.gravitateT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.gravitateL = tk.Label(text="Gravitate* ", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.vindecareT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.vindecareL = tk.Label(text="Rata vindecare* ", borderwidth=0, bg="gray5", font=self.app.helv14,
                                   fg="LightBlue")

        # tratament
        self.tratamentL = tk.Label(text="Tratament medicamentos", borderwidth=0, bg="gray5", font=self.app.helv14,
                                   fg="LightBlue")
        self.strTratament = tk.StringVar()
        self.tratamentC = tk.ttk.Combobox(app, width=int(self.app.width / 32), textvariable=self.strTratament)
        self.tratamentC['values'] = (' Da', ' Nu')  # se vor citi din baza de date

        self.tratamentC['state'] = 'readonly'

        self.suicidT = tk.Text(self.app, height=1, width=int(self.app.width / 40))
        self.suicidL = tk.Label(text="Rata suicid* ", borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")

        self.specificitateL = tk.Label(text="Specificitate gen", borderwidth=0, bg="gray5", font=self.app.helv14,
                                       fg="LightBlue")
        self.idAfectiuneStergereL = tk.Label(text="Numele afectiuni pentru care se vor sterge detaliile : ",
                                             borderwidth=0, bg="gray5", font=self.app.helv14, fg="LightBlue")
        self.strSpecificitate = tk.StringVar()
        self.specificitateC = tk.ttk.Combobox(app, width=int(self.app.width / 32), textvariable=self.strSpecificitate)
        self.specificitateC['values'] = (' Feminin', ' Masculin', ' Nu')  # se vor citi din baza de date
        self.specificitateC.bind("<<ComboboxSelected>>", selectSpecificitateGenDetalii)
        self.specificitateC['state'] = 'readonly'

        self.asistentaPersonalaL = tk.Label(text="Asistenta personala", borderwidth=0, bg="gray5", font=self.app.helv14,
                                            fg="LightBlue")
        self.strAsistentaPersonala = tk.StringVar()
        self.asistentaPersonalaC = tk.ttk.Combobox(app, width=int(self.app.width / 32),
                                                   textvariable=self.strAsistentaPersonala)
        self.asistentaPersonalaC['values'] = ('Da', 'Nu')  # se vor citi din baza de date
        self.asistentaPersonalaC.bind("<<ComboboxSelected>>", selectAsistentaPersonalaDetalii)
        self.asistentaPersonalaC['state'] = 'readonly'

        self.strAfectiunestr = tk.StringVar()
        self.afectiuneC = tk.ttk.Combobox(app, width=int(self.app.width / 32), textvariable=self.strAfectiunestr)

        self.valori = []
        valori1 = []

        app.cursor.execute('select id,nume from afectiune order by id asc')
        rows = app.cursor.fetchall()

        for i in rows:
            self.valori.append((i[0], i[1]))
            valori1.append(i[1])

        self.afectiuneC['values'] = valori1  # se vor citi din baza de date

        self.afectiuneC['state'] = 'readonly'
        self.idAfectiuneL = tk.Label(text="Nume afectiune*", borderwidth=0, bg="gray5", font=self.app.helv14,
                                     fg="LightBlue")

        self.nextB = tk.Button(master=self.app, text="Urmatoarele", width=15, height=1, bg="blue", fg="LightBlue",
                               font=self.app.helv14, background="grey10", borderwidth=1, command=nextTableDetalii)

        self.prevB = tk.Button(master=self.app, text="Anterioarele", width=15, height=1, bg="blue", fg="LightBlue",
                               font=self.app.helv14, background="grey10", borderwidth=1, command=prevTableDetalii)

        self.inserareGravitate = None
        self.inserareRataVindecare = None
        self.inserareTratament = None
        self.inserareRataSuicid = None
        self.inserareSpecificitateGen = None
        self.inserareAsistentaPersonala = None
        self.inserareIDAfectiune = None
        self.numeAfectiune = None

        self.table = []
        self.page = 0

    def ShowDetails(self, tip):
        self.lableDetalii.place(relx=0.5, rely=0.15, anchor='center')
        self.imgBrain.place(relx=0.8, rely=0.6, anchor='nw')
        self.operatieC.place(relx=0.45, rely=0.2, anchor='w')
        if tip == 1:
            self.adaugareL.place(relx=0.05, rely=0.2, anchor='w')
            self.afectiuneC.place(relx=0.20, rely=0.3, anchor='w')
            self.idAfectiuneL.place(relx=0.05, rely=0.3, anchor='w')
            self.gravitateT.place(relx=0.20, rely=0.35, anchor='w')
            self.gravitateL.place(relx=0.05, rely=0.35, anchor='w')
            self.vindecareT.place(relx=0.20, rely=0.40, anchor='w')
            self.vindecareL.place(relx=0.05, rely=0.40, anchor='w')
            self.tratamentC.place(relx=0.20, rely=0.45, anchor='w')
            self.tratamentL.place(relx=0.05, rely=0.45, anchor='w')
            self.suicidT.place(relx=0.20, rely=0.50, anchor='w')
            self.suicidL.place(relx=0.05, rely=0.50, anchor='w')
            self.specificitateL.place(relx=0.05, rely=0.55, anchor='w')
            self.specificitateC.place(relx=0.20, rely=0.55, anchor='w')
            self.asistentaPersonalaL.place(relx=0.05, rely=0.60, anchor='w')
            self.asistentaPersonalaC.place(relx=0.20, rely=0.60, anchor='w')
            self.adaugareB.place(relx=0.30, rely=0.70, anchor='w')
        elif tip == 0:
            self.vizualizareL.place(relx=0.05, rely=0.3, anchor='w')
            self.nextB.place(relx=0.2, rely=0.95, anchor='center')
            self.prevB.place(relx=0.4, rely=0.95, anchor='center')
        elif tip == 2:
            self.modificareL.place(relx=0.05, rely=0.3, anchor='w')
            self.afectiuneC.place(relx=0.20, rely=0.35, anchor='w')
            self.idAfectiuneL.place(relx=0.05, rely=0.35, anchor='w')
            self.gravitateT.place(relx=0.20, rely=0.40, anchor='w')
            self.gravitateL.place(relx=0.05, rely=0.40, anchor='w')
            self.vindecareT.place(relx=0.20, rely=0.45, anchor='w')
            self.vindecareL.place(relx=0.05, rely=0.45, anchor='w')
            self.tratamentC.place(relx=0.20, rely=0.50, anchor='w')
            self.tratamentL.place(relx=0.05, rely=0.50, anchor='w')
            self.suicidT.place(relx=0.20, rely=0.55, anchor='w')
            self.suicidL.place(relx=0.05, rely=0.55, anchor='w')
            self.specificitateL.place(relx=0.05, rely=0.60, anchor='w')
            self.specificitateC.place(relx=0.20, rely=0.60, anchor='w')
            self.asistentaPersonalaL.place(relx=0.05, rely=0.65, anchor='w')
            self.asistentaPersonalaC.place(relx=0.20, rely=0.65, anchor='w')
            self.modificaB.place(relx=0.30, rely=0.70, anchor='w')
            self.afisareInitialB.place(relx=0.5, rely=0.35)
        elif tip == 3:
            self.stergereL.place(relx=0.05, rely=0.3, anchor='w')
            self.afectiuneC.place(relx=0.15, rely=0.45, anchor='center')
            self.idAfectiuneStergereL.place(relx=0.05, rely=0.4, anchor='w')
            self.stergereB.place(relx=0.3, rely=0.45, anchor='w')  # aicisunt

    def HideDetails(self):
        self.afisareInitialB.place_forget()
        self.stergereL.place_forget()
        self.idAfectiuneStergereL.place_forget()
        self.stergereB.place_forget()
        self.vizualizareL.place_forget()
        self.modificareL.place_forget()
        self.stergereL.place_forget()
        self.lableDetalii.place_forget()
        self.imgBrain.place_forget()
        self.lableDetalii.place_forget()
        self.imgBrain.place_forget()
        self.adaugareL.place_forget()
        self.adaugareB.place_forget()
        self.adaugareB.place_forget()
        self.operatieC.place_forget()
        self.afectiuneC.place_forget()
        self.idAfectiuneL.place_forget()
        self.gravitateT.place_forget()
        self.gravitateL.place_forget()
        self.vindecareT.place_forget()
        self.vindecareL.place_forget()
        self.tratamentC.place_forget()
        self.tratamentL.place_forget()
        self.suicidT.place_forget()
        self.suicidL.place_forget()
        self.specificitateL.place_forget()
        self.specificitateC.place_forget()
        self.asistentaPersonalaL.place_forget()
        self.asistentaPersonalaC.place_forget()
        self.nextB.place_forget()
        self.modificaB.place_forget()
        self.prevB.place_forget()
        for i in self.table:
            i.place_forget()


class Menu:
    def __init__(self, app):
        # referire la fereastra de baza
        self.app = app
        # butoanele
        self.acasa = tk.Button(master=self.app, text="Acasa", width=int(self.app.width / 100), height=2, bg="blue",
                               fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=0,
                               command=self.onAcasa)
        self.fise = tk.Button(master=self.app, text="Fise pacienti", width=int(self.app.width / 100), height=2,
                              bg="blue", fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=0,
                              command=self.onFise)
        self.pacient = tk.Button(master=self.app, text="Pacienti", width=int(self.app.width / 100), height=2, bg="blue",
                                 fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=0,
                                 command=self.onPacienti)
        self.medicamente = tk.Button(master=self.app, text="Medicamente", width=int(self.app.width / 100), height=2,
                                     bg="blue", fg="LightBlue", font=self.app.helv14, background="grey10",
                                     borderwidth=0, command=self.onMedicamente)
        self.afectiuni = tk.Button(master=self.app, text="Afectiuni", width=int(self.app.width / 100), height=2,
                                   bg="blue", fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=0,
                                   command=self.onAfectiuni)
        self.psihiatri = tk.Button(master=self.app, text="Psihiatri", width=int(self.app.width / 100), height=2,
                                   bg="blue", fg="LightBlue", font=self.app.helv14, background="grey10", borderwidth=0,
                                   command=self.onPsihiatri)
        self.detaliiAfectiuni = tk.Button(master=self.app, text="Detalii afectiuni", width=int(self.app.width / 100),
                                          height=2, bg="blue", fg="LightBlue", font=self.app.helv14,
                                          background="grey10", borderwidth=0, command=self.onDetalii)
        self.button = [self.acasa, self.fise, self.pacient, self.medicamente, self.afectiuni, self.psihiatri,
                       self.detaliiAfectiuni]

    def ShowMenu(self):
        # afisare buotane
        for i in range(7):
            self.button[i].grid(row=0, column=i, padx=20, rowspan=2, pady=30)

        # adaugare functii callback la trecerea cu mouse-ul peste ele
        for i in self.button:
            i.bind("<Enter>", self.on_button)
            i.bind("<Leave>", self.off_button)

    def on_button(self, e):
        e.widget['background'] = 'LightBlue'
        e.widget['fg'] = 'grey10'

    def off_button(self, e):
        e.widget['background'] = 'grey10'
        e.widget['fg'] = 'LightBlue'

    def onAcasa(self, e=None):
        app.HideAll()
        app.home.ShowHome()

    def onFise(self, e=None):
        app.sheet.dataEliberareT.delete("1.0", "end")
        app.sheet.revenireControlT.delete("1.0", "end")
        app.sheet.pacientC.set('')
        app.sheet.medicC.set('')
        app.sheet.afectiuniLB.select_clear(0, tk.END)
        app.sheet.medicamenteLB.select_clear(0, tk.END)
        app.sheet.cantitateT.delete("1.0", "end")
        app.sheet.detaliiT.delete("1.0", "end")
        app.HideAll()
        app.sheet.ShowSheet(1)
        app.sheet.operatieC.current(1)

    def onPacienti(self, e=None):
        app.HideAll()
        app.patient.ShowPatient(1)
        app.patient.operatieC.current(1)

    def onAfectiuni(self, e=None):
        app.HideAll()
        app.disease.ShowDisease(1)
        app.disease.operatieC.current(1)

    def onMedicamente(self, e=None):
        app.HideAll()
        app.drugs.ShowDrugs(1)
        app.drugs.operatieC.current(1)

    def onPsihiatri(self, e=None):
        app.HideAll()
        app.doctor.ShowDoctor(1)
        app.doctor.operatieC.current(1)

    def onDetalii(self, e=None):
        app.details.operatieC.current(1)
        app.HideAll()
        selectOperatieDetalii(1)
        app.details.ShowDetails(1)


# fereastra principala
class App(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title('Cabinet de psihiatrie')

        # conectare server
        user = 'bd034'
        password = 'bd034'
        cx_Oracle.init_oracle_client(
            lib_dir="C:\\Users\\boaca\\Desktop\\Facultate\\BDP\\Interfata\\InterfataBD\\instantclient_21_7")
        dsn_tns = cx_Oracle.makedsn('bd-dc.cs.tuiasi.ro', '1539', service_name='orcl')
        self.conn = cx_Oracle.connect(user=user, password=password, dsn=dsn_tns)
        self.cursor = self.conn.cursor()

        # fond-uri folosite
        self.helv14 = font.Font(family='Cooper Black', size=14)
        self.helv16 = font.Font(family='Cooper Black', size=20, underline=True, slant='italic')
        self.helv30 = font.Font(family='Cooper Black', size=30)

        # configurare fereastra
        self.config(bg='gray5')
        self.width = self.winfo_screenwidth()
        self.height = self.winfo_screenheight()
        self.state("zoomed")  # maximizare fereastra

        # obiecte
        self.menu = Menu(self)
        self.details = PageDetails(self)
        self.doctor = PageDoctor(self)
        self.disease = PageDisease(self)
        self.sheet = PageSheet(self)
        self.drugs = PageDrugs(self)
        self.patient = PagePatient(self)
        self.home = PageHome(self)
        self.top = None

    def HideAll(self):
        self.patient.HidePatient()
        self.home.HideHome()
        self.sheet.HideSheet()
        self.drugs.HideDrugs()
        self.disease.HideDisease()
        self.doctor.HideDoctor()
        self.details.HideDetails()


if __name__ == "__main__":
    app = App()

    app.menu.ShowMenu()
    app.home.ShowHome()

    app.mainloop()
