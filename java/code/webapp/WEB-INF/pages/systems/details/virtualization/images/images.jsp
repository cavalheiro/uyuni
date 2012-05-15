<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://rhn.redhat.com/rhn" prefix="rhn" %>
<%@ taglib uri="http://rhn.redhat.com/tags/list" prefix="rl" %>

<html:xhtml/>
<html>

<head>
  <script type="text/javascript" src="/javascript/images.js"></script>
</head>

<body>
  <%@ include file="/WEB-INF/pages/common/fragments/systems/system-header.jspf" %>

  <rl:listset name="groupSet">
    <rhn:csrf />
    <html:hidden property="sid" value="${param.sid}" />

    <div id="images-table">
      <h2><bean:message key="images.jsp.select.title" /></h2>
      <div class="page-summary">
        <p>
          <bean:message key="images.jsp.select.summary" />
        </p>
      </div>
      <rl:list dataset="imagesList" emptykey="images.jsp.noimages">
        <rl:column headerkey="images.jsp.name" filterattr="name">
          <a href="javascript:void();" onclick="showForm('${current.id}','${current.name}','${current.version}','${current.arch}','${current.imageType.label}','${current.editUrl}');"><c:out value="${current.name}" /></a>
        </rl:column>
        <rl:column headerkey="images.jsp.version">
          <c:out value="${current.version}" />
        </rl:column>
        <rl:column headerkey="images.jsp.arch">
          <c:out value="${current.arch}" />
        </rl:column>
        <rl:column headerkey="images.jsp.type">
          <c:out value="${current.imageType.label}" />
        </rl:column>
      </rl:list>
    </div>

    <div id="deployment-form" style="display:none">
      <h2><bean:message key="images.jsp.deploy.title" /><a id="edit-link" href="" target="_blank"></a></h2>
      <div class="page-summary">
        <p><bean:message key="images.jsp.deploy.summary" /></p>
      </div>

      <table class="details-2-columns" width="100%">
        <tbody>
          <tr>
            <td width="50%">
              <h2><bean:message key="images.jsp.vmsetup" /></h2>
              <table class="details">
                <tr>
                  <th><bean:message key="images.jsp.vcpus" /></th>
                  <td><html:text property="vcpus" size="15" value="1" /></td>
                </tr>
                <tr>
                  <th><bean:message key="images.jsp.memory" /></th>
                  <td><html:text property="mem_mb" size="15" value="512" /></td>
                </tr>
                <tr>
                  <th><bean:message key="images.jsp.bridge" /></th>
                  <td><html:text property="bridge" size="15" value="br0" /></td>
                </tr>
              </table>
            </td>
            <td width="50%">
              <h2><bean:message key="images.jsp.proxyconfig" /></h2>
              <table class="details">
                <tr>
                  <th><bean:message key="images.jsp.proxyserver" /></th>
                  <td><html:text property="proxy_server" size="30" value="" /></td>
                </tr>
                <tr>
                  <th><bean:message key="images.jsp.proxyuser" /></th>
                  <td><html:text property="proxy_user" size="15" value="" /></td>
                </tr>
                <tr>
                  <th><bean:message key="images.jsp.proxypass" /></th>
                  <td><html:password property="proxy_pass" size="15" value="" /></td>
                </tr>
              </table>
            </td>
          </tr>
        </tbody>
      </table>

      <div align="right">
        <rhn:submitted />
        <html:hidden styleId="image-id" property="image_id" value="" />
        <hr />
        <html:submit onclick="showImages()">
          <bean:message key="images.jsp.cancel" />
        </html:submit>
        <html:submit property="dispatch"
                     disabled="${empty sessionScope.imagesList}">
          <bean:message key="images.jsp.dispatch" />
        </html:submit>
      </div>
    </div>
  </rl:listset>
</body>
</html>
