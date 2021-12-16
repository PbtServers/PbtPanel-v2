package com.pbtservers.pbtpanel.websocket.response;

import com.google.gson.JsonObject;

public class LoginRequired implements JSONOutput{
	private String message;
	
	public LoginRequired(String message) {
		this.message = message;
	}
	
	@Override
	public int getStatusCode() {
		return 401;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String toJSON() {
		JsonObject object = new JsonObject();
		object.addProperty("status", getStatusCode());
		object.addProperty("statusDescription", "Inicio de Sesión Necesario");
		object.addProperty("message", getMessage());
		return object.toString();
	}

}