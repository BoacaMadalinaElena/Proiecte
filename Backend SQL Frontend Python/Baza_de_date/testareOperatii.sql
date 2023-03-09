--!!!--vizualizare
SELECT * FROM afectiune;
SELECT * FROM pacient order by id desc;
SELECT * FROM psihiatru;
SELECT * FROM medicament;
SELECT * FROM detalii_afectiune;
SELECT * FROM fisa_medicala order by id;
SELECT * FROM fisa_afectiune_fk;
SELECT * FROM mdt_fisa_fk;

--nume afectiune + detalii
SELECT A.nume,D.gravitate,D.tratament_medicamentos,D.rata_suicid,D.specificitate_gen,D.asistenta_personala
FROM afectiune A,detalii_afectiune D;

--numarul de pacienti tratati de fiecare medic 
SELECT P.ID, COUNT(*) AS "numar pacienti" 
FROM fisa_medicala F ,psihiatru P 
WHERE P.ID = F.psihiatru_id
GROUP BY F.psihiatru_id,P.ID;

--numarul de fise medicale
SELECT COUNT(*) FROM fisa_medicala;

--numarul de fise pentru fiecare pacient
SELECT P.ID, COUNT(*) AS "numar fise" 
FROM fisa_medicala F ,pacient P 
WHERE P.ID = F.pacient_id
GROUP BY F.pacient_id,P.ID;

--data ultimului control pentru fiecare pacient
SELECT pacient_id,MAX(F.data_eliberare) AS "data ultimului control" 
FROM fisa_medicala F
GROUP BY pacient_id;

--******************************************************************************
--*pacientii unui psihiatru, pe care i-a consultat intr-o zi
select m.nume || ' ' ||  m.prenume as "Nume pshiatru",f.data_eliberare as "Data consult",m.nume || ' ' || p.prenume as "Nume pacient" 
from psihiatru m,pacient p , fisa_medicala f 
where f.psihiatru_id = m.id and f.pacient_id = p.id
order by m.nume,m.prenume,f.data_eliberare;

--*un alt select cu join-uri cu toate fisele medicale ale unui pacient 
select p.nume,p.prenume,p.numar_telefon as "Numar telefon pacient",f.id as "Numar fisa" ,f.data_eliberare,f.data_revenire_control
from pacient p , fisa_medicala f
where p.id = f.pacient_id
order by p.nume,p.prenume,f.id;

--*pacienti si afectiuni
select p.nume,p.prenume,p.numar_telefon,a.nume
from pacient p , fisa_afectiune_fk f_a,afectiune a,fisa_medicala f
where f_a.afectiune_id = a.id and f_a.fisa_medicala_id = f.id and p.id = f.pacient_id
order by p.nume,p.prenume,a.nume;

--*top afectiuni
select count(*) as "Numar de cazuri",a.nume
from fisa_afectiune_fk f_a, afectiune a
where f_a.afectiune_id = a.id
group by f_a.afectiune_id,a.nume
order by "Numar de cazuri";

--*top medicamente
select count(*) as "Numar de prescriptii", m.nume as "Nume medicament",m.firma_producatoare as "Firma producatoare"
from mdt_fisa_fk f_m , medicament m
where f_m.medicament_id = m.id
group by f_m.medicament_id, m.nume ,m.firma_producatoare
order by "Numar de prescriptii" desc;
--*****************************************************************************
--!!----pentru adaugare a se vedea inserareDate.sql

--!!----modificare 
UPDATE  pacient SET nume = 'Cimpoi' WHERE numar_telefon = '0767904534' ;
SELECT * FROM pacient;

UPDATE  pacient SET numar_telefon = '0756456678' WHERE numar_telefon = '0767904534' ;
SELECT * FROM pacient;

UPDATE  pacient SET adresa = 'Strada Grigorie Alexandrescu, Bistrita' WHERE numar_telefon = '0754674534' ;
SELECT * FROM pacient;

UPDATE  psihiatru SET nume = 'Atomei Vicu' WHERE numar_telefon = '0756467749' ;
SELECT * FROM psihiatru;

UPDATE  psihiatru SET numar_telefon = '0233445677' WHERE numar_telefon = '0233467671' ;
SELECT * FROM psihiatru;

UPDATE  psihiatru SET email = 'mariusCalin@gmail.com' WHERE numar_telefon = '0233341234' ;
SELECT * FROM psihiatru;

UPDATE medicament SET nume = 'Energyon' WHERE nume = 'Enerion' AND firma_producatoare = 'Antibiotice Iasi' ;
SELECT * FROM medicament;

UPDATE afectiune SET nume = 'Schizofrenie acuta' WHERE nume = 'Schizofrenie' ;
SELECT * FROM afectiune;

UPDATE detalii_afectiune SET gravitate = 9 WHERE afectiune_id = (SELECT ID FROM  afectiune WHERE nume = 'Delir') ;
SELECT * FROM detalii_afectiune;

UPDATE detalii_afectiune SET rata_vindecare = 19.8 WHERE afectiune_id = (SELECT ID FROM  afectiune WHERE nume = 'Fobie sociala') ;
SELECT * FROM detalii_afectiune;

UPDATE fisa_medicala SET data_revenire_control = '25-03-2023'  WHERE ID = 160 ;
SELECT * FROM fisa_medicala;

--!!----stergere 
DELETE FROM pacient WHERE numar_telefon = '0756435678';
DELETE FROM psihiatru WHERE numar_telefon = '0700041234';
DELETE FROM fisa_medicala WHERE ID = 300;
DELETE FROM medicament WHERE nume = 'Dulsevia' AND firma_producatoare = 'KRCA';
DELETE FROM afectiune WHERE nume = 'Pierderi de memorie';
DELETE FROM detalii_afectiune WHERE afectiune_id = (SELECT ID FROM afectiune WHERE nume = 'Delir');

--!!--validare(constrangeri)

INSERT INTO mdt_fisa_fk VALUES(400,120,3,'3 dimi');--fk eroare

INSERT INTO mdt_fisa_fk VALUES(100,420,3,'3 dimi');--fk eroare

INSERT INTO mdt_fisa_fk VALUES(100,120,10,'3 dimi');--dimensiune

INSERT INTO medicament VALUES(NULL,'',NULL);--vid

INSERT INTO medicament VALUES(NULL,'P',NULL);--dimensiune prea mica

INSERT INTO medicament VALUES(NULL,NULL,NULL);--not null

INSERT INTO psihiatru VALUES(NULL,'abvdc4','maria','0756467750','maria@gmail.com');--nume err

INSERT INTO psihiatru VALUES(NULL,'popa','maria1','0756467750','maria@gmail.com');--prenume err

INSERT INTO psihiatru VALUES(NULL,'popa','maria','9756467750','maria@gmail.com');--numar de telefon valori

INSERT INTO psihiatru VALUES(NULL,'popa','maria','975646750','maria@gmail.com');--numar dimensiune

INSERT INTO psihiatru VALUES(NULL,'popa','maria','975646750','maria@gmail.c');--email

INSERT INTO fisa_medicala VALUES(NULL,'9.5.2020','5.6.2023',1,100);--Fk 

INSERT INTO fisa_medicala VALUES(NULL,'9.5.2020','5.6.2023',200,10);--fk

INSERT INTO fisa_medicala VALUES(NULL,NULL,'5.6.2023',1,100);--not null

INSERT INTO fisa_medicala VALUES(NULL,'9.5.2020','5.6.2023',1,NULL);--not null

INSERT INTO pacient VALUES(NULL,'1Popa','Gigi','20.2.2000','0756467750','Masculin','Iasi');--nume

INSERT INTO pacient VALUES(NULL,'Popa','4Gigi','20.2.2000','0756467750','Masculin','Iasi');--prenume

INSERT INTO pacient VALUES(NULL,'Popa','Gigi','20.2.2024','0756467750','Masculin','Iasi');--data

INSERT INTO pacient VALUES(NULL,'Popa','Gigi','20.2.2020','07a6467750','Masculin','Iasi');--telefon

INSERT INTO pacient VALUES(NULL,'Popa','Gigi','20.2.2020','096467750','Masculin','Iasi');--telefon

INSERT INTO pacient VALUES(NULL,'Popa','Gigi','20.2.2020','096467750','Nu','Iasi');--gen

INSERT INTO fisa_afectiune_fk VALUES(400,100);--fk

INSERT INTO fisa_afectiune_fk VALUES(100,400);--fk

INSERT INTO afectiune VALUES(NULL,'Depresie');--unica

INSERT INTO afectiune VALUES(NULL,'De');--lungime

INSERT INTO detalii_afectiune VALUES(1,40.7,'Da',4,'Nu','Da',400);--fk

INSERT INTO detalii_afectiune VALUES(10,40.7,'Da',4,'Nu','Da',100);--dimensiune

INSERT INTO detalii_afectiune VALUES(1,4067.767,'Da',4,'Nu','Da',100);--dimensiune

INSERT INTO detalii_afectiune VALUES(1,40.7,'Pa',4,'Nu','Da',140);--valoare

INSERT INTO detalii_afectiune VALUES(1,40.7,'Da',4,'F','Da',140);--valoare

DELETE FROM pacient WHERE numar_telefon = '0712374534';--constrangere cheie externa

DELETE FROM psihiatru WHERE numar_telefon = '0233445677';--cheie externa

INSERT INTO afectiune VALUES(NULL,'Nevroza');--unique

INSERT INTO pacient VALUES(NULL,'Robu','Ion','25.6.1977','0754466578','Masculin','Nr 42, Strada Golia, Iasi');--unique

INSERT INTO psihiatru VALUES(NULL,'Munteanu','Marius Calin','0233341234',NULL);--unique

INSERT INTO medicament VALUES(NULL,'Dulsevia','KRCA');--unique


