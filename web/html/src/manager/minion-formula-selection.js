'use strict';

const React = require("react");
const ReactDOM = require("react-dom");

const Network = require("../utils/network");
const FormulaSelection = require("../components/formula-selection").FormulaSelection;
const capitalize = require("../utils/functions").Utils.capitalize;

function saveRequest(component, selectedFormulas) {
    const formData = {};
    formData.type = "SERVER";
    formData.id = serverId;
    formData.selected = selectedFormulas;

    return Network.post(
        "/rhn/manager/api/formulas/select",
        JSON.stringify(formData),
        "application/json"
    ).promise.then(data => {
        component.setState({
            messages: data
        });
    },
    (xhr) => {
        try {
            component.setState({
                errors: [JSON.parse(xhr.responseText)]
            })
        } catch (err) {
            component.setState({
                errors: [Network.errorMessageByStatus(xhr.status)]
            })
        }
    });
}

function addFormulaNavBar(formulaList) {
    $("#formula-nav-bar").remove();

    var navBar = "<ul class='nav nav-tabs nav-tabs-pf' id='formula-nav-bar'>\n"
    navBar += "<li class='active'><a href='/rhn/manager/systems/details/formulas?sid=" + serverId + "'>Formulas</a></li>\n";
    for (var i in formulaList)
        navBar += "<li><a href='/rhn/manager/systems/details/formula/" + i + "?sid=" + serverId + "'>" + capitalize(formulaList[i]) + "</a></li>\n";
    navBar += "</ul>"
    $(".spacewalk-content-nav").append(navBar);
}

ReactDOM.render(
  <FormulaSelection
    dataUrl={"/rhn/manager/api/formulas/list/SERVER/" + serverId}
    saveRequest={saveRequest}
    systemId={serverId}
    addFormulaNavBar={addFormulaNavBar} />,
  document.getElementById('formulas')
);
