Drop database if exists DBDentiSmile;

Create database DBDentiSmile;

Use DBDentiSmile;

Create table Pacientes(
	codigoPaciente int not null,
    nombresPaciente varchar(50) not null,
    apellidosPaciente varchar(50) not null,
    sexo char not null,
    fechaNacimiento date not null,
    direccionPaciente varchar(100) not null,
    telefonoPersonal varchar(8) not null,
    fechaPrimeraVisita date,
    primary key PK_codigoPaciente (codigoPaciente)
);

Create table Especialidades(
	codigoEspecialidad int not null auto_increment,
    descripcion varchar(100) not null,
    primary key PK_codigoEspecialidad (codigoEspecialidad)
);

Create table Medicamentos(
	codigoMedicamento int not null auto_increment,
    nombreMedicamento varchar(100) not null,
    primary key PK_codigoMedicamento (codigoMedicamento)
);

Create table Doctores(
	numeroColegiado int not null,
    nombresDoctor varchar(50) not null,
    apellidosDoctor varchar(50) not null,
    telefonoContacto varchar(8) not null,
    codigoEspecialidad int not null,
    primary key PK_numeroColegiado (numeroColegiado),
    constraint FK_Doctores_Especialidades foreign key (codigoEspecialidad) 
		references Especialidades (codigoEspecialidad)
);

Create table Recetas(
	codigoReceta int not null auto_increment,
    fechaReceta date not null,
    numeroColegiado int not null,
    primary key PK_codigoReceta (codigoReceta),
    constraint FK_Recetas_Doctores foreign key (numeroColegiado)
		references Doctores (numeroColegiado)
);

Create table DetalleReceta(
	codigoDetalleReceta int not null auto_increment,
    dosis varchar(100) not null,
    codigoReceta int not null,
    codigoMedicamento int not null,
    primary key PK_codigoDetalleReceta (codigoDetalleReceta),
    constraint FK_DetalleReceta_Recetas foreign key (codigoReceta)
		references Recetas (codigoReceta),
	constraint FK_DetalleReceta_Medicamentos foreign key (codigoMedicamento)
		references Medicamentos (codigoMedicamento)
);

Create table Citas(
	codigoCita int not null auto_increment,
    fechaCita date not null,
    horaCita time not null,
    tratamiento varchar(150),
    descripCondActual varchar(255) not null,
    codigoPaciente int not null,
    numeroColegiado int not null,
    primary key PK_codigoCita (codigoCita),
    constraint FK_Citas_Pacientes foreign key (codigoPaciente)
		references Pacientes (codigoPaciente),
	constraint FK_Citas_Doctores foreign key (numeroColegiado)
		references Doctores (numeroColegiado)
);

-- ------------------------------------------------------------------------------------------
-- --------------------------------PROCEDIMIENTOS ALMACENADOS--------------------------------
-- --------------------------------Pacientes--------------------------------
-- AGREGAR PACIENTE

Delimiter $$
	Create procedure sp_AgregarPaciente(in codigoPaciente int, in nombresPaciente varchar(50),
		in apellidosPaciente varchar(50), in sexo char, in fechaNacimiento date, in direccionPaciente varchar(100),
		in telefonoPersonal varchar(8), in fechaPrimeraVisita date)
        
        Begin
			Insert into Pacientes(codigoPaciente, nombresPaciente, apellidosPaciente, sexo, fechaNacimiento, direccionPaciente, 
				telefonoPersonal, fechaPrimeraVisita) 
                values (codigoPaciente, nombresPaciente, apellidosPaciente, upper(sexo), fechaNacimiento, direccionPaciente, 
					telefonoPersonal, fechaPrimeraVisita);
        End$$
Delimiter ;

call sp_AgregarPaciente(1001,'Jefferson Alexander','Calderon Martinez','m','2004-11-03','zona 21','12345678',now());

-- LISTAR PACIENTES

Delimiter $$
	Create procedure sp_ListarPacientes()
    
    Begin 
		Select 
			P.codigoPaciente, 
            P.nombresPaciente, 
            P.apellidosPaciente, 
            P.sexo, 
            P.fechaNacimiento, 
            P.direccionPaciente, 
            P.telefonoPersonal, 
            P.fechaPrimeraVisita from Pacientes P;
    End$$
Delimiter ;

call sp_ListarPacientes();

-- BUSCAR PACIENTE

Delimiter $$
	Create procedure sp_BuscarPaciente(in codPaciente int)
    
    Begin
		Select 
			P.codigoPaciente, 
            P.nombresPaciente, 
            P.apellidosPaciente, 
            P.sexo, 
            P.fechaNacimiento, 
            P.direccionPaciente, 
            P.telefonoPersonal, 
            P.fechaPrimeraVisita 
            from Pacientes P 
				where codigoPaciente = codPaciente;
    End$$
Delimiter ;

call sp_BuscarPaciente(1001);

-- ELIMINAR PACIENTE 

Delimiter $$
	Create procedure sp_EliminarPaciente(in codPaciente int)
    
    Begin
		Delete from Pacientes where codigoPaciente = codPaciente;
    End$$
Delimiter ;

call sp_EliminarPaciente(1001);
call sp_ListarPacientes();
call sp_AgregarPaciente(1001,'Jefferson Alexander','Calderon Martinez','m','2004-11-03','zona 21','12345678',now());

-- EDITAR PACIENTE
-- no acutalizamos ni llaves primaria ni foraneas
Delimiter $$ 
	Create procedure sp_EditarPaciente(in codPaciente int, in nombPaciente varchar(50), in apellPaciente varchar(50), 
		in sex char, in fechaNac date, in direcPaciente varchar(100), in telPersonal varchar(8), in fechaPV date)
        
        Begin
			Update Pacientes P
				set P.nombresPaciente = nombPaciente, P.apellidosPaciente = apellPaciente, P.sexo = upper(sex), P.fechaNacimiento = fechaNac,
                P.direccionPaciente = direcPaciente, P.telefonoPersonal = telPersonal, P.fechaPrimeraVisita = fechaPV
						where codigoPaciente = codPaciente;
        End$$
Delimiter ;

call sp_EditarPaciente(1001,'Jefferson Alexander','Calderon Martinez','m','2004-11-03','zona 11','12345678',now());
call sp_ListarPacientes();


-- --------------------------------Especialidades--------------------------------
-- AGREGAR ESPECIALIDAD
Delimiter $$
	Create procedure sp_AgregarEspecialidad(in descripcion varchar(100))
		Begin
			Insert into Especialidades(descripcion) 
				values (descripcion);
        End$$
Delimiter ;

call sp_AgregarEspecialidad("Blanqueado de dientes");
call sp_AgregarEspecialidad("Eliminación de caries");

-- LISTAR ESPECIALIDADES
Delimiter $$
	Create procedure sp_ListarEspecialidades()
		Begin
			Select
				E.codigoEspecialidad,
                E.descripcion
                from Especialidades E;
        End$$
Delimiter ;

call sp_ListarEspecialidades();

-- BUSCAR ESPECIALIDAD
Delimiter $$
	Create procedure sp_BuscarEspecialidad(in codEspecialidad int)
		Begin
			Select 
				E.codigoEspecialidad,
                E.descripcion
                from Especialidades E where codEspecialidad = E.codigoEspecialidad;
        End$$
Delimiter ;

call sp_BuscarEspecialidad(1);

-- ELIMINAR ESPECIALIDAD
Delimiter $$
	Create procedure sp_EliminarEspecialidad(in codEspecialidad int)
		Begin
			Delete from Especialidades 
				where codEspecialidad = codigoEspecialidad;
        End$$
Delimiter ;

call sp_EliminarEspecialidad(2);
call sp_ListarEspecialidades();

-- EDITAR ESPECIALDIAD
Delimiter $$
	Create procedure sp_EditarEspecialidad(in codEspecialidad int, in descr varchar(100))
		Begin
			Update Especialidades E
				set E.descripcion = descr where E.codigoEspecialidad = codEspecialidad;
        End$$
Delimiter ;	

call sp_EditarEspecialidad(1, "Blanqueamiento");
call sp_ListarEspecialidades();


-- --------------------------------Medicamentos--------------------------------
-- AGREGAR MEDICAMENTO
Delimiter $$
	Create procedure sp_AgregarMedicamento(in nombreMedicamento varchar(100))
		Begin
			Insert into Medicamentos(nombreMedicamento)
				values (nombreMedicamento);
        End$$
Delimiter ;

call sp_AgregarMedicamento("Amoxicilina");
call sp_AgregarMedicamento("Sulbactam");

-- LISTAR MEDICAMENTOS
Delimiter $$
	Create procedure sp_ListarMedicamentos()
		Begin
			Select
				M.codigoMedicamento,
                M.nombreMedicamento
                from Medicamentos M;
        End$$
Delimiter ;

call sp_ListarMedicamentos();

-- BUSCAR MEDICAMENTOS
Delimiter $$
	Create procedure sp_BuscarMedicamento(in codMedicamento int)
		Begin
			Select	
				M.codigoMedicamento,
                M.nombreMedicamento
                from Medicamentos M where M.codigoMedicamento = codMedicamento;
        End$$
Delimiter ;

call sp_BuscarMedicamento(2);

-- ELIMINAR MEDICAMENTO
Delimiter $$
	Create procedure sp_EliminarMedicamento(in codMedicamento int)
		Begin
			Delete from Medicamentos 
				where codigoMedicamento = codMedicamento;
        End$$
Delimiter ;	

call sp_EliminarMedicamento(2);
call sp_ListarMedicamentos();

-- EDITAR MEDICAMENTO
Delimiter $$
	Create procedure sp_EditarMedicamento(in codMedicamento int, in nomMedicamento varchar(100))
		Begin 
			Update Medicamentos M
				set M.nombreMedicamento = nomMedicamento where M.codigoMedicamento = codMedicamento;
        End$$
Delimiter ;

call sp_EditarMedicamento(1, "Penicilina");
call sp_ListarMedicamentos();


-- --------------------------------Doctores--------------------------------
-- AGREGAR DOCTOR
Delimiter $$
	Create procedure sp_AgregarDoctor(in numeroColegiado int, in nombresDoctor varchar(50), in apellidosDoctor varchar(50), in telefonoContacto varchar(8), in codigoEspecialidad int)
		Begin
			Insert into Doctores(numeroColegiado, nombresDoctor, apellidosDoctor, telefonoContacto, codigoEspecialidad)
				values (numeroColegiado, nombresDoctor, apellidosDoctor, telefonoContacto, codigoEspecialidad);
        End$$
Delimiter ;

call sp_AgregarDoctor(303001, "Angel", "Marroquin", "12345687", 1);
call sp_AgregarDoctor(203400, "Katherine", "Flores", "12093144", 1);

-- LISTAR DOCTORES
Delimiter $$
	Create procedure sp_ListarDoctores()
		Begin
			Select
				D.numeroColegiado,
                D.nombresDoctor,
                D.apellidosDoctor,
                D.telefonoContacto,
                D.codigoEspecialidad
                from Doctores D;
        End$$
Delimiter ;

call sp_ListarDoctores();

-- BUSCAR DOCTOR
Delimiter $$
	Create procedure sp_BuscarDoctor(in numColegiado int)
		Begin
			Select 
				D.numeroColegiado,
				D.nombresDoctor,
				D.apellidosDoctor,
				D.telefonoContacto,
				D.codigoEspecialidad
				from Doctores D where D.numeroColegiado = numColegiado;
		End$$
Delimiter ;

call sp_BuscarDoctor(303001);

-- ELIMINAR DOCTOR
Delimiter $$
	Create procedure sp_EliminarDoctor(in numColegiado int)
		Begin
			Delete from Doctores 
				where numeroColegiado = numColegiado;
        End$$
Delimiter ;

call sp_EliminarDoctor(303001);
call sp_ListarDoctores();

-- EDITAR DOCTOR
Delimiter $$
	Create procedure sp_EditarDoctor(in numColegiado int, in nomDoc varchar(50), in apeDoc varchar(50), in telContacto varchar(8))
		Begin
			Update Doctores D
				set D.nombresDoctor = nomDoc, D.apellidosDoctor = apeDoc, D.telefonoContacto = telContacto
                where D.numeroColegiado = numColegiado;
        End$$
Delimiter ;

call sp_EditarDoctor(203400, "Katherine", "Flores", "12345678");
call sp_ListarDoctores();


-- --------------------------------Recetas--------------------------------
-- Agregar Receta
Delimiter $$
	Create procedure sp_AgregarReceta(in fechaReceta date, in numeroColegiado int)
		Begin
			Insert into Recetas(fechaReceta, numeroColegiado)
				values (fechaReceta, numeroColegiado);
        End$$
Delimiter ;

call sp_AgregarReceta(now(), 203400);
call sp_AgregarReceta(now(), 203400);

-- Listar Recetas
Delimiter $$
	Create procedure sp_ListarRecetas()
		Begin
			Select
				R.codigoReceta,
                R.fechaReceta,
                R.numeroColegiado
                from Recetas R;
        End$$
Delimiter ;

call sp_ListarRecetas();

-- Buscar Receta
Delimiter $$
	Create procedure sp_BuscarReceta(in codReceta int)
		Begin
			Select
				R.codigoReceta,
                R.fechaReceta,
                R.numeroColegiado
				from Recetas R where R.codigoReceta = codReceta;
        End$$
Delimiter ;

call sp_BuscarReceta(2);

-- Eliminar Receta
Delimiter $$
	Create procedure sp_EliminarReceta(in codReceta int)
		Begin
			Delete from Recetas 
				where codigoReceta = codReceta;
        End$$
Delimiter ;

call sp_EliminarReceta(2);
call sp_ListarRecetas();

-- Editar Receta
Delimiter $$
	Create procedure sp_EditarReceta(in codReceta int, in fchReceta date)
		Begin
			Update Recetas R
				set R.fechaReceta = fchReceta where R.codigoReceta = codReceta;
		End$$
Delimiter ;

call sp_EditarReceta(1, "2022-04-22");
call sp_ListarRecetas();


-- --------------------------------Detalle Receta--------------------------------
-- Agregar Detalle Receta
Delimiter $$
	Create procedure sp_AgregarDetalleReceta(in dosis varchar(100), in codigoReceta int, in codigoMedicamento int)
		Begin
			Insert into DetalleReceta(codigoDetalleReceta, dosis, codigoReceta, codigoMedicamento)
				values (codigoDetalleReceta, dosis, codigoReceta, codigoMedicamento);
        End$$
Delimiter ;

call sp_AgregarReceta(now(), 203400);
call sp_AgregarDetalleReceta("1 pastilla cada día", 1, 1);
call sp_AgregarDetalleReceta("1 pastilla cada día", 3, 1);

-- Listar Detalles Receta
Delimiter $$
	Create procedure sp_ListarDetallesReceta()
		Begin
			Select
				DR.codigoDetalleReceta,
                DR.dosis,
                DR.codigoReceta,
                DR.codigoMedicamento
                from DetalleReceta DR;
        End$$
Delimiter ;

call sp_ListarDetallesReceta();

-- Buscar Detalle Receta
Delimiter $$
	Create procedure sp_BuscarDetalleReceta(in codDetalleReceta int)
		Begin
			Select
				DR.codigoDetalleReceta,
                DR.dosis,
                DR.codigoReceta,
                DR.codigoMedicamento
                from DetalleReceta DR where DR.codigoDetalleReceta = codDetalleReceta;
        End$$
Delimiter ;

call sp_BuscarDetalleReceta(2);

-- Eliminar Detalle Receta
Delimiter $$
	Create procedure sp_EliminarDetalleReceta(in codDetalleReceta int)
		Begin
			Delete from DetalleReceta
				where codigoDetalleReceta = codDetalleReceta;
        End$$
Delimiter ;

call sp_EliminarDetalleReceta(2);
call sp_ListarDetallesReceta();

-- Editar Detalle Receta
Delimiter $$
	Create procedure sp_EditarDetalleReceta(in codDetalleReceta int, in dose varchar(100))
		Begin
			Update DetalleReceta DR
				set DR.dosis = dose where DR.codigoDetalleReceta = codDetalleReceta;
        End$$
Delimiter ;

call sp_EditarDetalleReceta(1, "2 pastillas cada semana");
call sp_ListarDetallesReceta();


-- --------------------------------Citas--------------------------------
-- Agregar Cita
Delimiter $$
	Create procedure sp_AgregarCita(in fechaCita date, in horaCita time, in tratamiento varchar(150), in descripCondActual varchar(255), in codigoPaciente int, in numeroColegiado int)
		Begin
			Insert into Citas(fechaCita, horaCita, tratamiento, descripCondActual, codigoPaciente, numeroColegiado)
				values (fechaCita, horaCita, tratamiento, descripCondActual, codigoPaciente, numeroColegiado);
        End$$	
Delimiter ;

call sp_AgregarCita(now(), now(), "Blanqueamiento", "No especificada", 1001, 203400);
call sp_AgregarCita(now(), now(), "Quita de caries",  "No especificada", 1001, 203400);

-- Listar citas
Delimiter $$
	Create procedure sp_ListarCitas()
		Begin
			Select
				C.codigoCita,
                C.fechaCita,
                C.horaCita,
                C.tratamiento,
                C.descripCondActual,
                C.codigoPaciente,
                C.numeroColegiado
                from Citas C;
        End$$
Delimiter ;

call sp_ListarCitas();

-- Buscar Cita
Delimiter $$
	Create procedure sp_BuscarCita(in codCita int)
		Begin
			Select
				C.codigoCita,
                C.fechaCita,
                C.horaCita,
                C.tratamiento,
                C.descripCondActual,
                C.codigoPaciente,
                C.numeroColegiado
                from Citas C where C.codigoCita = codCita;
        End$$
Delimiter ;

call sp_BuscarCita(2);

-- Eliminar Cita
Delimiter $$
	Create procedure sp_EliminarCita(in codCita int)
		Begin
			Delete from Citas
				where codigoCita = codCita;
        End$$
Delimiter ;

call sp_EliminarCita(2);
call sp_ListarCitas();

-- Editar Cita
Delimiter $$
	Create procedure sp_EditarCita(in codCita int, in dateCita date, in hourCita time, in treatment varchar(150), in descrCondActl varchar(255))
		Begin
			Update Citas C
				set C.fechaCita = dateCita, C.horaCita = hourCita, C.tratamiento = treatment, C.descripCondActual = descrCondActl
                where C.codigoCita = codCita;
        End$$
Delimiter ;	

call sp_EditarCita(1, now(), "15:30:00", "Blanqueamiento", "No especificada");
call sp_ListarCitas();





Create table Usuario(
	codigoUsuario int not null auto_increment,
    nombreUsuario varchar(100) not null,
    apellidoUsuario varchar(100) not null,
    usuarioLogin varchar(50) not null,
    contrasena varchar(50) not null,
    primary key PK_codigoUsuario (codigoUsuario)
);


-- ------------------------------------- AGREGAR USUARIO -------------------------------------
Delimiter $$
	Create procedure sp_AgregarUsuario(in nombreUsuario varchar(100), in apellidoUsuario varchar(100), in usuarioLogin varchar(50), in contrasena varchar(50))
    Begin
		insert into Usuario(nombreUsuario, apellidoUsuario, usuarioLogin, contrasena)
			values (nombreUsuario, apellidoUsuario, usuarioLogin, contrasena);
    End$$
Delimiter ;

-- ------------------------------------- LISTAR USUARIO -------------------------------------

Delimiter $$
	Create procedure sp_ListarUsuarios()
	Begin
		Select
			U.codigoUsuario, 
            U.nombreUsuario,
            U.apellidoUsuario, 
            U.usuarioLogin, 
            U.contrasena
            from Usuario U;
    End$$
Delimiter ;

call sp_AgregarUsuario("Jefferson", "Calderon", "jcalderon", "123");
call sp_AgregarUsuario("Jose", "Ortega", "jortega", "@123");
call sp_ListarUsuarios();

Create table Login(
	usuarioMaster varchar(50) not null,
    passwordLogin varchar(50) not null,
    primary key PK_usuarioMaster (usuarioMaster)
);


