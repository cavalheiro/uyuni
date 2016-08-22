# -*- coding: utf-8 -*-
'''
Retrieve SUSE Manager pillar data by the ID. File format is: <prefix>_<minion_id>.sls
Adds formula pillar data.

Parameters:
    path - Path where SUSE Manager stores pillar data
    formula_layout_path - Path were SUSE Manager stores formula layouts
    formula_data_path - Path were SUSE Manager stores formula data

.. code-block:: yaml

    ext_pillar:
      - suma_minion: /another/path/with/the/pillar/files

'''

# Import python libs
from __future__ import absolute_import
import os
import logging
import yaml
import json

# Set up logging
log = logging.getLogger(__name__)


def __virtual__():
    '''
    Ensure the pillar module name.
    '''
    return True


def ext_pillar(minion_id, pillar, path,
    formula_layout_path="/usr/share/susemanager/salt/formulas",
    formula_data_path="/srv/susemanager/formulas_data"):
    '''
    Find SUMA-related pillars for the registered minions and return the data.
    '''

    log.debug('Getting pillar data for the minion "{0}"'.format(minion_id))

    ret = dict()
    data_filename = os.path.join(path, 'pillar_{minion_id}.yml'.format(minion_id=minion_id))
    try:
        ret = yaml.load(open(data_filename).read())
    except Exception as error:
        log.error('Error accessing "{pillar_file}": {message}'.format(pillar_file=data_filename, message=str(error)))

    try:
        ret.update(formula_pillars(minion_id, ret.get("group_ids", []), formula_layout_path, formula_data_path))
    except Exception as error:
        log.error('Error accessing formula pillar data: {message}'.format(message=str(error)))

    return ret

def formula_pillars(minion_id, group_ids, formula_layout_path, formula_data_path):
    '''
    Find formula pillars for the minion, merge it and return the data.
    '''
    ret = dict()
    formulas = dict()
    with open(os.path.join(formula_data_path, "group_formulas.json")) as f:
        group_formulas = json.load(f)
        for group in group_ids:
            formulas[group] = group_formulas.get(unicode(group), [])

	formulas_list = []
    for key in formulas:
        for formula in formulas[key]:
        	formulas_list.append(formula)
            ret.update(load_formula_pillar(minion_id, key, formula, formula_layout_path, formula_data_path))
	ret["formulas"] = formulas_list
    return ret

def load_formula_pillar(minion_id, group_id, formula_name, formula_layout_path, formula_data_path):
    '''
    Load the data from a specific formula for a minion in a specific group, merge and return it.
    '''
    layout_filename = os.path.join(formula_layout_path, formula_name, "form.yml")
    group_filename = os.path.join(formula_data_path, "group_pillar", str(group_id) + "_" + formula_name + ".json")
    system_filename = os.path.join(formula_data_path, "pillar", str(minion_id) + "_" + formula_name + ".json")
    try:
        layout = yaml.load(open(layout_filename).read())
        group_data = json.load(open(group_filename)) if os.path.isfile(group_filename) else {}
        system_data = json.load(open(system_filename)) if os.path.isfile(system_filename) else {}
    except Exception as error:
        log.error('Error loading data for formula "{formula}": {message}'.format(formula=formula_name, message=str(error)))
        return dict()

    return merge_formula_data(layout, group_data, system_data)

def merge_formula_data(layout, group_data, system_data, scope="system"):
    '''
    Merge the group and system formula data, respecting the scope of a value.
    '''
    ret = {}

    for element_name in layout:
        if element_name.startswith("$"):
            continue

        value = None
        element = layout[element_name]
        element_scope = element.get("$scope", scope)

        if element.get("$type", "text") in ["group", "hidden-group"]:
            value = merge_formula_data(element, group_data.get(element_name, {}), system_data.get(element_name, {}), element_scope)
        elif element_scope == "system":
            value = system_data.get(element_name, group_data.get(element_name, element.get("$default", None)))
        elif element_scope == "group":
            value = group_data.get(element_name, element.get("$default", None))
        elif element_scope == "readonly":
            value = element.get("$default", None)

        if value != None:
            ret[element_name] = value
    return ret
