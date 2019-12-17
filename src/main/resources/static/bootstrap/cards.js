let allEntities = [];
let allRegions = [];
let allCategories = [];
let allFacilities = [];
let cls;

read("/regions").then(x => allRegions = x);
read("/categories").then(x => allCategories = x);
read("/facilities").then(x => allFacilities = x);

async function requestServer(suffix, method, entity) {
    let reqProps = {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        }
    };
    if (method !== "GET" && entity !== undefined)
        reqProps["body"] = JSON.stringify(entity);
    return (await fetch(suffix, reqProps)).json();
}

async function create(suffix, entity) {
    console.log("saving " + JSON.stringify(entity));
    return await requestServer(suffix, "POST", entity);
}

async function read(suffix) {
    return await requestServer(suffix, "GET", null);
}

async function update(suffix, entity) {
    return await requestServer(suffix, "PUT", entity);
}

async function deleteRequest(suffix, id) {
    return await requestServer(suffix + "/"+id, "DELETE");
}

class Region {
    constructor({regionName, regionId, imageUrl}) {
        this.regionName = regionName;
        this.regionId = regionId;
        this.imageUrl = imageUrl;
    }

    header() {
        return this.regionName;
    }

    id() {
        return this.regionId;
    }

    suffixPost() {
        return "/region";
    }
}

class Category {
    constructor({catName, imageUrl}) {
        this.catName = catName;
        this.imageUrl = imageUrl;
    }

    id() {
        return this.catName;
    }

    header() {
        return this.catName;
    }

    suffixPost() {
        return "/category"
    }
}

class Facility {
    constructor({_id, name, description, lat, lng, imageUrl, categories, region}) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.imageUrl = imageUrl;
        this.region = region;
        this.categories = null;
    }

    id() {
        return this._id;
    }

    header() {
        return this.name;
    }

    suffixPost() {
        return "/facility";
    }
}

class SingleSelectionDropdown {
    constructor(sourceCls, entityIndex, fieldName, source) {
        this.sourceCls = sourceCls;
        this.entityIndex = entityIndex;
        this.fieldName = fieldName;
        this.source = source;
    }

    createDropDownCode() {
        let contents = "";
        for (let i = 0; i < this.source.length; i++) {
            contents += this.createDropDownRow(this.source[i]);
        }
        let sourceItem = allEntities[this.entityIndex][this.fieldName];
        let header = sourceItem == null ? "null" : new this.sourceCls(sourceItem).header();
        return `
<label>${this.fieldName}</label>
    <div class="btn-group">
        <button class="btn btn-secondary dropdown-toggle" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" 
        id="${this.fieldName}Input" cv='${JSON.stringify(this.source[this.entityIndex])}'>${header}</button>
    <div class="dropdown-menu">
        ${contents}
    </div>
    </div>`;
    }

    createDropDownRow(value) {
        return `
<a class="dropdown-item" href="#" 
onclick='SingleSelectionDropdown.onSelect(${this.entityIndex}, "${this.fieldName}", ${JSON.stringify(value)}, ${this.sourceCls})'>
${new this.sourceCls(value).header()}</a>`;
    }

    static onSelect(entityIndex, fieldName, value, sourceCls) {
        let field = document.getElementById(fieldName + "Input");
        field.setAttribute("cv", JSON.stringify(value));
        field.innerText = new sourceCls(value).header();
    }
}

function formEntityList(entities) {
    let cards = "";
    for (let i = 0; i < entities.length; i++) {
        cards += simpleListItem(entities[i], i);
    }
    document.getElementById("entityList").innerHTML = cards;
}

function simpleListItem(content, index) {
    content = new cls(content);
    return `<li class="nav-item"><button class="btn btn-primary" 
style="margin: 5px" onclick="formEntity(${index})">${content.header()}</button>
<button class="btn btn-danger" style="margin: 4px" 
onclick="deleteRequest('${content.suffixPost()}', ${content.id()})">-
</button></li>`
}

function formEntity(index) {
    let entity = allEntities[index];
    document.getElementById("entityFormHolder").innerHTML = getEditingArea(entity);
}

function getEditingArea(entity) {
    let code = "";
    let index = allEntities.indexOf(entity);
    let properties = Object.getOwnPropertyNames(entity);
    code += createHeader(entity);
    for (let i = 0; i < properties.length; i++) {
        let property = properties[i];
        let value = entity[property];

        code += (property.includes("region") && entity._id !== undefined) ?
            new SingleSelectionDropdown(Region, index, "region", allRegions).createDropDownCode()
            : createTextEditor(property, value);
    }
    code += createSubmitButton();
    code += createFooter(entity);
    return code;
}

async function captureEntity() {
    let ret = {};
    let properties = Object.getOwnPropertyNames(new cls({}));
    for (let i = 0; i < properties.length; i++) {
        let property = properties[i];
        let element = document.getElementById(property + "Input");
        let value;
        if (element.tagName === "INPUT")
            value = element.value;
        else if (element.tagName === "BUTTON")
            value = JSON.parse(element.getAttribute("cv"));
        else value = null;

        if (value === "null")
            value = null;

        ret[property] = value;
    }
    let retx = new cls(ret);
    return await update(retx.suffixPost(), retx);
}

function createHeader(entity) {
    return `<h3> ${new cls(entity).header()} #<span id="ID">${new cls(entity).id()}</span></h3>`
}

function createDescriptionEditor(propertyName) {
    return '<label for="regionDescInput">Region description</label>' +
        '<textarea class="form-control" placeholder="enter region description"></textarea>';
}

function createTextEditor(property, value) {
    let id = property + "Input";
    return `<label for="${id}">${property}</label> <input type="text" class="form-control" id="${id}" placeholder="enter region name" value="${value}">`;
}

function createSubmitButton() {
    return `<button type="submit" class="btn btn-primary" onclick="captureEntity()">Save</button>`
}

function createFooter() {
    return `</form>`;
}

function workWithEntities(_cls) {
    allEntities = [];
    document.getElementById("entityFormHolder").innerHTML = "";
    cls = _cls;
    if (cls === Region) {
        allEntities = allRegions;
    }
    else if (cls === Category)
        allEntities = allCategories;
    else if (cls === Facility)
        allEntities = allFacilities;
    formEntityList(allEntities.map (x => new cls(x)));
}
