CREATE TABLE t_usuario (
    id_i        SERIAL PRIMARY KEY,
    ci          VARCHAR(10) NOT NULL,
    login       VARCHAR(15) NOT NULL,
    password    VARCHAR(40) NOT NULL,
    nombre      VARCHAR(30) NOT NULL,
    email_1     VARCHAR(50) NOT NULL,
    email_2     VARCHAR(50),
    bloqueo     VARCHAR(1) DEFAULT 'n',
    intentos_c  INT NOT NULL,
    intentos_d  INT NOT NULL,
    creacion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP /*DATATIME 'SYSDATE'*/

/* ,CONSTRAINT "i_dpf" FOREIGN KEY ("ci") REFERENCES t_dpfr(ci)
    CONSTRAINT "i_cred" FOREIGN KEY ("ci") REFERENCES t_credr(credci),
    CONSTRAINT "i_cda" FOREIGN KEY ("ci") REFERENCES t_cdar(ci)*/

);

/*La presencia de los indices ayudan a:
- reforzar la integridad referncial con el uso de una clave, es decir que no existan duplicados en este campo o indice (UNIQUE).
- facilitar el ordenamiento basado en su contenido y
- a optimizar la velocidad de las sentencias

cuando una tabla es eliminada todos los indices asociados con la tabla son elimimnados
pero se lo haria con DROP INDEX login_index */

CREATE UNIQUE INDEX login_index ON t_usuario(login);
CREATE UNIQUE INDEX nombre_index ON t_usuario(nombre);

CREATE TABLE t_tipo (
    tipo        INT NOT NULL PRIMARY KEY,
    descripcion VARCHAR(15)
);

CREATE TABLE t_registro (
    id_log  SERIAL PRIMARY KEY,
    id_i    INT, /* REFERENCES t_usuario(id_i) ON DELETE CASCADE, */
    tipo    INT REFERENCES t_tipo(tipo),
    datos   VARCHAR(255),
    ip      VARCHAR(15),
    fecha   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE t_adm (
    llave       VARCHAR(112),
    llave_log   VARCHAR(112)
/*    fecha   TIMESTAMP DEFAULT CURRENT_TIMESTAMP */
);


CREATE TABLE t_eliminados (
    id_i        INT PRIMARY KEY,
    ci          VARCHAR(10) NOT NULL,
    login       VARCHAR(15) NOT NULL,
    password    VARCHAR(40) NOT NULL,
    nombre      VARCHAR(30) NOT NULL,
    email_1     VARCHAR(50) NOT NULL,
    email_2     VARCHAR(50),
    bloqueo     VARCHAR(1) DEFAULT 'n',
    intentos_c  INT NOT NULL,
    intentos_d  INT NOT NULL,
    creacion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


/************************** DEPOSITOS A PLAZO FIJO ****************************/
CREATE TABLE t_dpf (
    dpf       INT PRIMARY KEY,
    apertura  VARCHAR(9),/*01-dic-99*/
    plazo     INT,
    vcto      VARCHAR(9), /*01-dic-00*/
    capital   FLOAT,
    tasa      FLOAT,/*FLOAT DE PRECISION SIMPLE PARA UNA PRECISION DOBLE EMPLEAR DOUBLE O FLOAT(25 A 53)*/
    periodo   VARCHAR(10),  /*Unico , Mensual , Trimestral , Semestral , Otro  */
    moneda    VARCHAR(4), /*$us. ,  Bs. */
    caja_a    INT  /* ++++++++++++ */
);

CREATE TABLE t_dpfr (
    dpf  INT REFERENCES t_dpf(dpf),
    ci   VARCHAR(10)
);
CREATE UNIQUE INDEX dpf_index ON t_dpfr(dpf,ci);

CREATE TABLE t_dpfq (
   dpf        INT REFERENCES t_dpf(dpf),
   fecha      VARCHAR(10), /*12/10/1996 */
   operacion  VARCHAR(50), /*Operacion */
   d_h        VARCHAR(1),  /*Debe o Haber*/
   monto      FLOAT
);

CREATE TABLE t_dpfa (
  dpf         INT REFERENCES t_dpf(dpf),
  fecha       VARCHAR(10), /* 12/10/1996 */
  monto_int   FLOAT
);


/********************************  CREDITOS  **********************************/
CREATE TABLE t_cred (
    cred        INT PRIMARY KEY,
    monto       FLOAT,
    tasa        FLOAT,
    plazo       INT,
    tipocuota   VARCHAR(8), /* C.FIJA, S/SALDOS */
    tipoamort   VARCHAR(8), /* DIAS-F., F.FIJA */
    diasamort   INT,
    f_desemb    VARCHAR(10), /* 12/10/1996 */
    saldo       FLOAT,
    caja_a      INT  /* 132052  pero en realidad es ctacble*/
);

CREATE TABLE t_credr (
    cred  INT REFERENCES t_cred(cred),
    ci    VARCHAR(10)
);
--CREATE UNIQUE INDEX t_credr_index ON t_credr(cred,ci)

/*concepto de pago*/
CREATE TABLE t_credq (
   cred       INT REFERENCES t_cred(cred),
   fecha      VARCHAR(10), /*12/10/1996 */
   operacion  VARCHAR(50), /*Operacion */
   d_h        VARCHAR(1),  /*Debe o Haber*/
   monto      FLOAT,
   amort      FLOAT,
   interes    FLOAT,
   otros      FLOAT,
   monto_pag  FLOAT,
   saldo_cap  FLOAT
);

/*plan de pagos  Samir.- otros */
CREATE TABLE t_creda (
  cred        INT REFERENCES t_cred(cred),
  fecha       VARCHAR(10),
  capital     FLOAT,
  interes     FLOAT,
  formularios FLOAT,
  total_pag   FLOAT,
  saldo_cap   FLOAT
);


/********************************CAJA DE AHORROS*******************************/
CREATE TABLE t_cda (
    cda       INT PRIMARY KEY,
    moneda    VARCHAR(4),
    interes   FLOAT,
    periodo   VARCHAR(10),     /*Unico , Mensual , Trimestral , Semestral , Otro  */
    saldo     FLOAT,
    inpp      FLOAT,
    fecha     VARCHAR(10)
);

CREATE TABLE t_cdar (
    cda  INT REFERENCES t_cda(cda),
    ci   VARCHAR(10)
);
--CREATE UNIQUE INDEX t_cdar_index ON t_cdar(cda,ci)

CREATE TABLE t_cdaq (
   cda        INT REFERENCES t_cda(cda),
   fecha      VARCHAR(10), /*12/10/1996 */
   operacion  VARCHAR(50), /*Operacion */
   d_h        VARCHAR(1),  /*Debe o Haber*/
   monto      FLOAT
);

/******************************************************************************
*******************************************************************************/
/*
load data local infile "dpf.txt" into table t_dpf;
load data local infile "cred.txt" into table t_cred;
load data local infile "cda.txt" into table t_cda;*/

COPY t_dpf FROM '/cygdrive/d/apache/src/business/src/postgresql/dpf.txt';
COPY t_cred FROM '/cygdrive/d/apache/src/business/src/postgresql/cred.txt';
COPY t_cda FROM '/cygdrive/d/apache/src/business/src/postgresql/cda.txt';

INSERT INTO t_tipo (tipo,descripcion) VALUES (1,'AUTENTIFICACION');
INSERT INTO t_tipo (tipo,descripcion) VALUES (2,'CREACION');
INSERT INTO t_tipo (tipo,descripcion) VALUES (3,'INVALIDO');
INSERT INTO t_tipo (tipo,descripcion) VALUES (4,'MODIFICACION');
INSERT INTO t_tipo (tipo,descripcion) VALUES (5,'SALIDA');
INSERT INTO t_tipo (tipo,descripcion) VALUES (6,'ELIMINADO');
INSERT INTO t_tipo (tipo,descripcion) VALUES (7,'VACIADO');

INSERT INTO t_usuario (ci,login,password,nombre,email_1,email_2,bloqueo,intentos_c,intentos_d)
VALUES ('4467616','ivan','aafdb6e5bb411d5d0ebbd4feecdfb1af', 'Paniagua Monroy Ivan','paniagur@estudiantes.ucbcba.edu.bo','bak@bigfoot.com', 'n',10 , 10);

INSERT INTO t_usuario (ci,login,password,nombre,email_1,email_2,bloqueo,intentos_c,intentos_d)
VALUES ('767038','antezanal','2b7b10774e36faa3a432def8887429b4', 'Antezana Ledezma Laura','unkn@supernet.com.bo','', 'n',3 , 3);

INSERT INTO t_usuario (ci,login,password,nombre,email_1,email_2,bloqueo,intentos_c,intentos_d)
VALUES ('3613718','rivero_m','f8d7ceb1a201753bd211fa073103dbca', 'Rivero Bellido Mariela','rivero_cbcba@hotmail.com','', 'n',3 , 3);

INSERT INTO t_usuario (ci,login,password,nombre,email_1,email_2,bloqueo,intentos_c,intentos_d)
VALUES ('3621143','doradoolm','a0a864b5c5a316766debaa3721e3a683', 'Dorado Uzeda Olmos','benigna_cbba@email.com','', 'n',3 , 3);

INSERT INTO t_usuario (ci,login,password,nombre,email_1,email_2,bloqueo,intentos_c,intentos_d)
VALUES ('28283222','rojasq','2d5e874b870cd13ce524820ea4048f95', 'Rojas Quiroga Manuel','rojas_q@supernet.com.bo','', 'n',3 , 3);

INSERT INTO t_usuario (ci,login,password,nombre,email_1,email_2,bloqueo,intentos_c,intentos_d)
VALUES ('28283222','arevalomm','6c3b5a024613e9c15f4febfd190124ac', 'Arevalo Encinas Jaime','arevaloq@supernet.com.bo','', 'n',3 , 3);

INSERT INTO t_usuario (ci,login,password,nombre,email_1,email_2,bloqueo,intentos_c,intentos_d)
VALUES ('4467616','mit_kev','65e6ca0e8150f97fb4e7b24adefa4bd4', 'Mitnick Kevin','unkn@bigfoot.com','', 'n',3 , 3);

/*si es que hago que comiencen con uno*/
INSERT INTO t_registro (id_i, tipo, datos, ip) VALUES (1,2,'creacion','local');
INSERT INTO t_registro (id_i, tipo, datos, ip) VALUES (2,2,'creacion','local');
INSERT INTO t_registro (id_i, tipo, datos, ip) VALUES (3,2,'creacion','local');
INSERT INTO t_registro (id_i, tipo, datos, ip) VALUES (4,2,'creacion','local');
INSERT INTO t_registro (id_i, tipo, datos, ip) VALUES (5,2,'creacion','local');
INSERT INTO t_registro (id_i, tipo, datos, ip) VALUES (6,2,'creacion','local');
INSERT INTO t_registro (id_i, tipo, datos, ip) VALUES (7,2,'creacion','local');

/*tiene que comenzar con algun valor*/
INSERT INTO t_adm (llave,llave_log) VALUES ('948510b38526d380c48389e0257f6bc4fbb06e1afeef3d02','948510b38526d380c48389e0257f6bc4fbb06e1afeef3d02');

INSERT INTO t_dpfr (dpf, ci) VALUES (1, '4467616');
INSERT INTO t_dpfr (dpf, ci) VALUES (4, '4467616');
INSERT INTO t_dpfr (dpf, ci) VALUES (5, '4467616');

INSERT INTO t_dpfr (dpf, ci) VALUES (2, '3613718');
INSERT INTO t_dpfr (dpf, ci) VALUES (3, '3613718');


INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (1,'20/01/2001','Apertura de Cuenta','h',1000.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (1,'19/02/2001','Pago de Intereses','d',40.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (1,'21/03/2001','Pago de Intereses','d',40.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (1,'20/04/2001','Pago de Intereses','d',40.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (1,'20/05/2001','Pago de Intereses','d',40.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (1,'19/06/2001','Pago de Intereses','d',40.0);

INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (2,'20/01/2001','Apertura de Cuenta','h',45300.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (2,'19/02/2001','Pago de Intereses','d',70.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (2,'21/03/2001','Pago de Intereses','d',70.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (2,'20/04/2001','Pago de Intereses','d',70.0);

INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (3,'20/01/2001','Apertura de Cuenta','h',15300.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (3,'19/02/2001','Pago de Intereses','d',90.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (3,'21/03/2001','Pago de Intereses','d',90.0);

INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (4,'20/01/2001','Apertura de Cuenta','h',45300.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (4,'19/02/2001','Pago de Intereses','d',70.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (4,'21/03/2001','Pago de Intereses','d',70.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (4,'20/04/2001','Pago de Intereses','d',70.0);

INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (5,'20/01/2001','Apertura de Cuenta','h',15300.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (5,'19/02/2001','Pago de Intereses','d',90.0);
INSERT INTO t_dpfq (dpf,fecha,operacion,d_h,monto) VALUES (5,'21/03/2001','Pago de Intereses','d',90.0);


INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (1,'19/02/2001',40.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (1,'21/03/2001',40.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (1,'20/04/2001',40.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (1,'20/05/2001',40.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (1,'19/06/2001',40.0);

INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (2,'19/02/2001',60.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (2,'21/03/2001',60.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (2,'20/04/2001',60.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (2,'20/05/2001',60.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (2,'19/06/2001',60.0);

INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (3,'19/02/2001',140.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (3,'21/03/2001',140.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (3,'20/04/2001',140.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (3,'20/05/2001',140.0);

INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (4,'19/02/2001',80.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (4,'21/03/2001',80.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (4,'20/04/2001',80.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (4,'20/05/2001',80.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (4,'19/06/2001',80.0);

INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (5,'19/02/2001',100.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (5,'21/03/2001',100.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (5,'20/04/2001',100.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (5,'20/05/2001',100.0);
INSERT INTO t_dpfa (dpf,fecha, monto_int) VALUES (5,'19/06/2001',100.0);



INSERT INTO t_credr(cred,ci) VALUES (12, '4467616');
INSERT INTO t_credr(cred,ci) VALUES (17, '4467616');

INSERT INTO t_credr(cred,ci) VALUES (103, '3613718');

INSERT INTO t_credr(cred,ci) VALUES (108, '767038');
INSERT INTO t_credr(cred,ci) VALUES (115, '767038');


INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (12,'20/01/2001','Apertura de Prestamo','h',3000.0,0,0,0,0,0);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (12,'19/02/2001','Amortizacion','d',0,60,30,10,100,2940);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (12,'21/03/2001','Amortizacion','d',0,50,35,5,90,2890);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (12,'20/04/2001','Amortizacion','d',0,50,35,5,90,2890);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (12,'20/05/2001','Amortizacion','d',0,50,35,5,90,2790);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (12,'19/06/2001','Amortizacion','d',0,50,35,5,90,2690);

INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (17,'20/01/2001','Apertura de Prestamo','h',5000.0,0,0,0,0,0);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (17,'19/02/2001','Amortizacion','d',0,60,30,10,100,4940);

INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (103,'20/01/2001','Apertura de Prestamo','h',3000.0,0,0,0,0,0);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (103,'19/02/2001','Amortizacion','d',0,60,30,10,100,2940);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (103,'21/03/2001','Amortizacion','d',0,50,35,5,90,2890);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (103,'20/04/2001','Amortizacion','d',0,50,35,5,90,2890);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (103,'20/05/2001','Amortizacion','d',0,50,35,5,90,2790);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (103,'19/06/2001','Amortizacion','d',0,50,35,5,90,2690);

INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (108,'20/01/2001','Apertura de Prestamo','h',5000.0,0,0,0,0,0);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (108,'19/02/2001','Amortizacion','d',0,60,30,10,100,4940);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (17,'21/03/2001','Amortizacion','d',0,50,35,4,90,4890);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (17,'20/04/2001','Amortizacion','d',0,70,35,7,90,4890);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (17,'20/05/2001','Amortizacion','d',0,50,35,9,90,4790);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (17,'19/06/2001','Amortizacion','d',0,50,35,15,100,4690);

INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (115,'20/01/2001','Apertura de Prestamo','h',3000.0,0,0,0,0,0);
INSERT INTO t_credq (cred,fecha,operacion,d_h,monto,amort,interes,otros,monto_pag,saldo_cap) VALUES (115,'19/02/2001','Amortizacion','d',0,60,30,10,100,2940);



INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (12,'19/02/2001',40.0,30,3,93,2940);
INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (12,'21/03/2001',40.0,30,3,93,2940);
INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (12,'20/04/2001',40.0,30,3,93,2940);
INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (12,'20/05/2001',40.0,30,3,93,2940);
INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (12,'19/06/2001',40.0,30,3,93,2940);

INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (17,'19/02/2001',30.0,30,3,93,2940);
INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (17,'21/03/2001',30.0,30,3,93,2940);
INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (17,'20/04/2001',30.0,30,3,93,2940);
INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (17,'20/05/2001',30.0,30,3,93,2940);
INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (17,'19/06/2001',30.0,30,3,93,2940);

INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (103,'19/02/2001',40.0,30,3,93,2940);
INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (103,'21/03/2001',40.0,30,3,93,2940);
INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (103,'20/04/2001',40.0,30,3,93,2940);
INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (103,'20/05/2001',40.0,30,3,93,2940);
INSERT INTO t_creda (cred,fecha,capital,interes,formularios,total_pag,saldo_cap) VALUES (103,'19/06/2001',40.0,30,3,93,2940);


INSERT INTO t_cdar(cda,ci) VALUES (64, '3613718');
INSERT INTO t_cdar(cda,ci) VALUES (66, '3613718');

INSERT INTO t_cdar(cda,ci) VALUES (67, '4467616');

INSERT INTO t_cdar(cda,ci) VALUES (14, '3621143');
INSERT INTO t_cdar(cda,ci) VALUES (15, '3621143');

INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (64,'20/01/2001','Apertura de Cuenta','h',45300.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (64,'19/02/2001','Deposito en efectivo','h',10200.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (64,'21/03/2001','Pago en Efectivo','d',66.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (64,'20/04/2001','Pago en Efectivo','d',230.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (64,'20/04/2001','Pago en Efectivo','d',230.0);

INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (66,'20/01/2001','Apertura de Cuenta','h',45300.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (66,'19/02/2001','Deposito en efectivo','h',10200.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (66,'21/03/2001','Pago en Efectivo','d',66.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (66,'20/04/2001','Pago en Efectivo','d',230.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (66,'20/04/2001','Pago en Efectivo','d',230.0);

INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (67,'20/01/2001','Apertura de Cuenta','h',45300.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (67,'19/02/2001','Deposito en efectivo','h',10200.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (67,'21/03/2001','Pago en Efectivo','d',66.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (67,'20/04/2001','Pago en Efectivo','d',230.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (67,'20/04/2001','Pago en Efectivo','d',230.0);

INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (14,'20/01/2001','Apertura de Cuenta','h',15300.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (14,'19/02/2001','Pago de Intereses','d',90.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (14,'21/03/2001','Pago de Intereses','d',90.0);

INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (15,'20/01/2001','Apertura de Cuenta','h',15300.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (15,'19/02/2001','Pago de Intereses','d',90.0);
INSERT INTO t_cdaq (cda,fecha,operacion,d_h,monto) VALUES (15,'21/03/2001','Pago de Intereses','d',90.0);

