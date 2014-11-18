<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<span class="obs-field">
	<span class="obs-label">${ concept.name.name }</span>:
	<c:choose>
		<c:when test="${ !readOnly }">
			<c:choose>
				<c:when test="${ concept.datatype.text }">
					<input id="${ id }" name="${ name }" type="text" value="<c:out value="${ initialValue }"/>" />
				</c:when>
				<c:when test="${ concept.datatype.numeric }">
					<input
						id="${ id }" name="${ name }" type="text" size="5"
						value="${ initialValue }"
						onblur="checkNumber(this, '${ id }-error', ${ concept.precise ? "true" : "false" }, ${ concept.lowAbsolute != null ? concept.lowAbsolute : "null" }, ${ concept.hiAbsolute != null ? concept.hiAbsolute : "null" })"
					/>
					<span class="obs-units">${ concept.units }</span>
				</c:when>
				<c:when test="${ concept.datatype.coded }">
					<select name="${ name }">
						<option value=""></option>
						<c:forEach items="${ concept.answers }" var="answer">
							<option value="${ answer.answerConcept.conceptId }" ${ answer.answerConcept == initialValue ? "selected='selected'" : "" }>${ answer.answerConcept.name.name }</option>
						</c:forEach>
					</select>
				</c:when>
				<c:otherwise>
					<span class="error">Concept datatype not supported</span>
				</c:otherwise>
			</c:choose>
			<span id="${ id }-error" class="error field-error" style="display: none"></span>
			<input type="hidden" name="${ name }-errorid" value="${ id }-error" />
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${ concept.datatype.text }">
					<span class="value">${ initialValue }</span>
				</c:when>
				<c:when test="${ concept.datatype.numeric }">
					<span class="value">${ initialValue }</span>
					<span class="obs-units">${ concept.units }</span>
				</c:when>
				<c:when test="${ concept.datatype.coded }">
					<span class="value">${ initialValue.name.name }</span>
				</c:when>
				<c:otherwise>
					<span class="error">Concept datatype not supported</span>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</span>