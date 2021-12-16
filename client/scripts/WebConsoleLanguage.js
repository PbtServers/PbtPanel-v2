/**
 WebConsole Language Manager for WebConsole
 Used to save your preferred language into your browser
 https://github.com/mesacarlos
 2019-2020 Carlos Mesa under MIT License.
*/
function setLanguage(locale){
	//Save to persistence
	persistenceManager.setLanguage(locale);
	//Set locale phrases
	switch(locale){
		case "es_ES":
			lang = {
				"navbarHomeLink": "Inicio",
				"home_header": "Selecciona un Servidor del Menú",
				"home_description": "Usa la barra superior para añadir un nuevo Servidor de Minecraft o para Conectarte a un Servidor añadido Previamente.",
				"serversDropdown": "Tus Servidores",
				"add_server": "Añadir Servidor",
				"noServersAdded": "Ningún Servidor Guardado",
				"lang_dropdown": "Idioma",
				"addServerModalLongTitle": "Añadir un nuevo Servidor",
				"addServerModalSvName": "Nombre del Servidor:",
				"addServerModalSvIp": "IP del Servidor:",
				"addServerModalSvPort": "Puerto PbtPanelv2:",
				"addServerModalSvSsl": "SSL está Activado",
				"addServerModalSslAdvice": "Te estás conectando al cliente por HTTPS, por tanto SSL es obligatorio",
				"addServerModalClose": "Cerrar",
				"saveAndConnectServerButton": "Guardar y Conectar",
				"passwordModalLongTitle": "Se Necesita Contraseña",
				"passwordModalLabel": "Contraseña:",
				"passwordModalRememberLabel": "Recordar contraseña",
				"passwordModalCloseButton": "Cerrar",
				"passwordSendButton": "Iniciar Sesión",
				"disconnectionModalLongTitle": "Desconectado/a",
				"disconnectionModalDescription": "Se Perdió la conexión con el Servidor. Esto puede deberse a:",
				"disconnectionModalsub1": "El Servidor ha sido Cerrado por Petición.",
				"disconnectionModalsub2": "El puerto no está Abierto en el Host. Si tienes Dudas, Contacta con Pbt!",
				"disconnectionModalCloseButton": "Cerrar",
				"disconnectionModalWelcomeScreenButton": "Volver a la Página de Inicio",
				"settingsLink": "Configuración",
				"settingsModalLongTitle": "Configuración de PbtPanelv2",
				"showDateSettingsSwitchLabel": "Mostrar Hora en cada Linea de PbtPanelv2",
				"readLogFileSwitchLabel": "Leer log completo al iniciar sesión",
				"settingsModalCloseButton": "Hecho",
				"players_online": "Jugadores/as en Línea",
				"cpu_title": "CPU",
				"ram_title": "RAM en uso",
				"user_title": "Iniciado Sesión como",
				"deleteServerButton": "Borrar Servidor",
				"sendCommandButton": "Enviar"
			}
			break;
		default:
			console.error("Idioma no Elegido");
	}

	//Set phrases
	jQuery.each(lang, (key, value) =>{
		try{
			document.getElementById(key).textContent = value;
		}catch(err){
			console.error("No se Puede Traducir " + key + " (" + value + ")")
		}
	});

}
