let allEntities = [];
let allRegions = [];
let allCategories = [];
let allFacilities = [];
let cls;

function nullate(obj) {

}

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
    let ret = await requestServer(suffix, "POST", entity);
    await workWithEntities(cls);
    return ret;
}

async function read(suffix) {
    return await requestServer(suffix, "GET", null);
}

async function update(suffix, entity) {
    let ret = await requestServer(suffix, "PUT", entity);
    await workWithEntities(cls);
    return ret;
}

async function deleteRequest(suffix, id) {
    await requestServer(suffix + "/" + id, "DELETE");
    await workWithEntities(cls);
}

class Region {
    constructor({regionName, regionId, imageUrl}) {
        this.regionName = regionName;
        this.regionId = regionId;
        this.imageUrl = imageUrl;
        nullate(this)
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
    constructor({catId, catName, imageUrl}) {
        if (catId === undefined)
            this.catId = 0;
        else
            this.catId = catId;
        this.catName = catName;
        this.imageUrl = imageUrl;
        nullate(this)
    }

    id() {
        return this.catId;
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
        nullate(this)
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
    <div class="btn-group" style="margin: 10px 5px 5px;">
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
    cards += createAddButton();
    document.getElementById("entityList").innerHTML = cards;
}

function simpleListItem(content, index) {
    content = new cls(content);
    return `<li class="nav-item"><button class="btn btn-primary" 
style="margin: 5px" onclick="formEntity(${index})">${content.header()}</button>
<button class="btn btn-danger" style="margin: 4px" 
onclick="deleteRequest('${content.suffixPost()}', '${content.id()}')">-
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
        try {
            if (element.tagName === "INPUT")
                value = element.value;
            else if (element.tagName === "BUTTON")
                value = JSON.parse(element.getAttribute("cv"));
            else value = null;

            if (value === "null")
                value = null;

            ret[property] = value;
        } catch (e) {
            console.debug(`${element.id}: ${element.tagName}`);
        }
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
    return `<label for="${id}" style="margin-top: 10px">${property}</label> <input type="text" class="form-control" id="${id}" placeholder="enter ${property}" value="${value}">`;
}

function createSubmitButton() {
    return `<button type="submit" class="btn btn-primary" onclick="captureEntity()">Save</button>`
}

function createAddButton() {
    return `<li class="nav-item"><button class="btn btn-secondary" 
style="margin: 5px" onclick="addEntity()">Add new Entity</button></li>`
}

function addEntity() {
    let obj = new cls({});
    create(obj.suffixPost(), obj);
}

function createFooter() {
    return `</form>`;
}

async function workWithEntities(_cls) {
    await read("/regions").then(x => allRegions = x);
    await read("/categories").then(x => allCategories = x);
    await read("/facilities").then(x => allFacilities = x);
    allEntities = [];
    document.getElementById("entityFormHolder").innerHTML = "";
    cls = _cls;
    if (cls === Region) {
        allEntities = allRegions;
    } else if (cls === Category)
        allEntities = allCategories;
    else if (cls === Facility)
        allEntities = allFacilities;
    formEntityList(allEntities.map(x => new cls(x)));
}
