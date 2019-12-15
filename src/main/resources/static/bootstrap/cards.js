var allRegions;

async function requestServer(suffix, method, entity) {
    const response = await fetch(`/`+suffix, {
        method: method,
        body: JSON.stringify(entity),
        headers: {
            'Content-Type': 'application/json'
        }
    });
    return await response.json(); //extract JSON from the http response
}

async function create(suffix) {
    return await requestServer(suffix, "POST", this);
}

async function read(suffix) {
    return await requestServer(suffix, "GET", null);
}

async function update(suffix) {
    return await requestServer(suffix, "PUT", this);
}

function Region(regionName, regionId, imageUrl) {
    this.regionName = regionName;
    this.regionId = regionId;
    this.imageUrl = imageUrl;

    function header() {
        return regionName;
    }

    function suffixPost() {
        return "/region";
    }

    function suffixGet() {
        return "/regions";
    }
}

function Category(catName, imageUrl) {
    this.catName = catName;
    this.imageUrl = imageUrl;

    function header() {
        return catName;
    }

    function suffixPost() {
        return "/category"
    }

    function suffixGet() {
        return "/categories"
    }
}

function Facility(_id, name, description, lat, lng, imageUrl) {
    this._id = _id;
    this.name = name;
    this.description = description;
    this.lat = lat;
    this.lng = lng;
    this.imageUrl = imageUrl;

    function header() {
        return name;
    }

    function suffixPost() {
        return "/facility";
    }

    function suffixGet() {
        return "/facilities";
    }
}

function formList(regionsJson) {
    let cards = "";
    for (let i = 0; i < regionsJson.length; i++) {
        cards += simpleListItem(regionsJson[i].header() + " # " + regionsJson[i].regionId, i);
    }
    document.getElementById("regionList").innerHTML = cards
}

function simpleListItem(content, index) {
    return `<li class="nav-item"><button class="btn btn-primary" style="margin: 5px" onclick="formRegion(${index})">${content}</button></li>`
}

function formRegion(index) {
    let region = allRegions[index];
    document.getElementById("regionFormHolder").innerHTML = regionEditTemplate(region);
}

function regionEditTemplate(region) {
    return `
        <h3> Region #<span id="regionId">${region.regionId}</span></h3>
        <form>
          <div class="form-group">
            <label for="regionNameInput">Region name</label>
            <input type="input" class="form-control" id="regionNameInput" placeholder="enter region name"
            value="${region.regionName}">
          </div>
          <button type="submit" class="btn btn-primary" onclick="editRegion()">Save</button>
        </form>`
}


async function getRegions() {
    const response = await fetch(`/regions`);
    allRegions = await response.json(); //extract JSON from the http response
    return allRegions;
}

async function editRegion() {
    const response = await fetch(`/region`, {
        method: 'PUT',
        body: JSON.stringify({
            "regionId": document.getElementById("regionId").innerHTML,
            "regionName": document.getElementById("regionNameInput").value,
            "imageUrl": "null"
        }), // string or object
        headers: {
            'Content-Type': 'application/json'
        }
    });
    const myJson = await response.json(); //extract JSON from the http response
    // do something with myJson
    console.log("edited: " + myJson);
}

function descriptionEditor(propertyName) {
    return '<label for="regionDescInput">Region description</label>' +
        '<textarea class="form-control" placeholder="enter region description"></textarea>';
}

function getEditingArea(entity) {
    let code = "";
    for (let i = 0; i < entity.length; i++) {
        property = entity[i];
        switch (property) {
            case "desc":
                code += descriptionEditor(property.name);
                break;
            case "text":
                code += textEditor();
        }
    }
}

getRegions().then(formList);
