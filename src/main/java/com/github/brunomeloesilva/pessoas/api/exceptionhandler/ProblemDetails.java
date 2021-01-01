package com.github.brunomeloesilva.pessoas.api.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ProblemDetails {
	
	private Integer status;
	private String title;
	private String detail;
	private List<InvalidParams> invalidParams; 
	private String type;
	private OffsetDateTime timestamp;
	
	public static class InvalidParams {
		private String propriedade;
		private String erro;
		
		public InvalidParams(String propriedade, String erro) {
			super();
			this.propriedade = propriedade;
			this.erro = erro;
		}

		public String getPropriedade() {
			return propriedade;
		}

		public void setPropriedade(String propriedade) {
			this.propriedade = propriedade;
		}

		public String getErro() {
			return erro;
		}

		public void setErro(String erro) {
			this.erro = erro;
		}
	}
	
	public ProblemDetails(Integer status, OffsetDateTime timestamp, String title, String detail, List<InvalidParams> invalidParams) {
		super();
		this.status = status;
		this.title = title;
		this.detail = detail;
		this.timestamp = timestamp;
		this.invalidParams = invalidParams;
		this.type = "http://www.errors.com.br";
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public OffsetDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(OffsetDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public List<InvalidParams> getErrorList() {
		return invalidParams;
	}

	public void setErrorList(List<InvalidParams> invalidParams) {
		this.invalidParams = invalidParams;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
