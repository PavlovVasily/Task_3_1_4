const tableBodyAllUsers = document.body.querySelector('.tableBodyAllUsers')
const tableBodyCurUser = document.body.querySelector('.tableBodyCurUser')
const modalDeleteUser = document.body.querySelector('#deleteModal')
const modalUpdateUser = document.body.querySelector('#updateModal')
const formAddUser = document.querySelector('.addUserForm')
const formDeleteUser = document.querySelector('.deleteUserForm')
const formUpdateUser = document.querySelector('.updateUserForm')
const authId = document.body.querySelector('#curID')

let user = ''
let idCur
let output = ''

const URL = 'http://localhost:8080/api/users'

// Functions
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

const renderCurUserTable = (user) => {
    output = ''
    const roles = []
    user.roles.forEach(role => roles.push(role.role))
    output += `
            <tr data-id="${user.id}"">
                <td scope="row">${user.id}</td>
                <td scope="row">${user.name}</td>
                <td th:text="">${user.age}</td>
                <td><p align="left">${user.email}</p></td>
                <td>
                    ${roles}
                </td>
            </tr>     
            `;
    tableBodyCurUser.innerHTML = output
}

const renderTable = (users) => {
    output = ''
    users.forEach(user => {
        const roles = []
        user.roles.forEach(role => roles.push(role.role))
        output += `
            <tr data-id="${user.id}"">
                <td scope="row">${user.id}</td>
                <td scope="row">${user.name}</td>
                <td th:text="">${user.age}</td>
                <td><p align="left">${user.email}</p></td>
                <td>
                    ${roles}
                </td>
                <td>
                    <button type="button" class="btn btn-primary btn-sm mt-2" data-toggle="modal" data-target="#updateModal" id="updateUser">
                        Изменить
                    </button>
                </td>
                <td>
                    <button type="button" class="btn btn-danger btn-sm mt-2" data-toggle="modal" data-target="#deleteModal" id="deleteUser">
                        Удалить
                    </button>
                </td>
            </tr>     
            `;
    });
    tableBodyAllUsers.innerHTML = output
}

// Get - Read the all users
// Method: Get
fetch(URL)
    .then(res => res.json())
    .then(data => {
        renderTable(data)
        console.log(data)
        tableBodyAllUsers.addEventListener('click', e => {
            e.preventDefault()

            let delButtonIsPressed = e.target.id === 'deleteUser'
            let updButtonIsPressed = e.target.id === 'updateUser'

            // Get - Read the cur user
            // Method: Get

            if (delButtonIsPressed) {
                idCur = e.target.parentNode.parentElement.dataset.id

                fetch(URL + '/' + idCur)
                    .then(res => res.json())
                    .then(data => {
                        user = data
                    })
            }

            if (updButtonIsPressed) {
                idCur = e.target.parentNode.parentElement.dataset.id

                fetch(URL + '/' + idCur)
                    .then(res => res.json())
                    .then(data => {
                        user = data
                    })
            }

            $('#deleteModal').on('shown.bs.modal', function (e) {
                //sleep(100).then(() => {
                modalDeleteUser.querySelector('input[name=id]').value = user.id
                modalDeleteUser.querySelector('input[name=name]').value = user.name
                modalDeleteUser.querySelector('input[name=age]').value = user.age
                modalDeleteUser.querySelector('input[name=email]').value = user.email
                const deselectedRoles = [...modalDeleteUser.querySelector('select').options]
                    .forEach(option => option.selected = false)

                const selectedRoles = [...modalDeleteUser.querySelector('select').options]
                    .filter(option => user.roles.some(role => option.value === role.role))
                    .forEach(option => option.selected = 'selected')
            })

            $('#updateModal').on('shown.bs.modal', function (e) {
                // sleep(100).then(() => {
                modalUpdateUser.querySelector('input[name=id]').value = user.id
                modalUpdateUser.querySelector('input[name=name]').value = user.name
                modalUpdateUser.querySelector('input[name=age]').value = user.age
                modalUpdateUser.querySelector('input[name=email]').value = user.email
                modalUpdateUser.querySelector('input[name=password]').value = ''
                modalUpdateUser.querySelector('input[name=passwordConfirm]').value = ''
                const deselectedRoles = [...modalUpdateUser.querySelector('select').options]
                    .forEach(option => option.selected = false)

                const selectedRoles = [...modalUpdateUser.querySelector('select').options]
                    .filter(option => user.roles.some(role => option.value === role.role))
                    .forEach(option => option.selected = 'selected')
                // })
            })
        })
    })
    .then(() => {
        idCur = authId.textContent
        console.log(idCur)
        fetch(URL + '/' + idCur)
            .then(res => res.json())
            .then(data => {
                renderCurUserTable(data)
            })
    })

// Update - update user
// Method: PUT
formUpdateUser.addEventListener('submit', (e) => {
    e.preventDefault()

    // Формирование ролей
    const selectedRoles = [...formUpdateUser.elements.roleNames.options].filter(option => option.selected).map(option => option.value)
    const roles = [];
    for (let i = 0; i < selectedRoles.length; i++) {
        roles.push({
            role: selectedRoles[i]
        });
    }

    //Отправляем запрос
    fetch(URL, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            id: formUpdateUser.elements.id.value,
            name: formUpdateUser.elements.name.value,
            age: formUpdateUser.elements.age.value,
            email: formUpdateUser.elements.email.value,
            password: formUpdateUser.elements.password.value,
            passwordConfirm: formUpdateUser.elements.passwordConfirm.value,
            roles: roles
        })
    })

        // получаем ответ на запрос
        .then(res => res.json())

        //Скрыть модальное окно
        .then(() => {
            $('#updateModal').modal('hide')
        })

        //Обновить таблицу
        .then(() => {
            fetch(URL)
                .then(res => res.json())
                .then(data => {
                    renderTable(data)
                })
        })

        // Обновить таблицу пользователя
        .then(() => {
            idCur = authId.textContent
            fetch(URL + '/' + idCur)
                .then(res => res.json())
                .then(data => {
                    renderCurUserTable(data)
                })
        })

})

// Delete - delete user
// Method: Delete
formDeleteUser.addEventListener('submit', (e) => {
    e.preventDefault()

    fetch(`${URL}/${idCur}`, {method: 'DELETE'})
        .then(res => res.text())
        .then(res => console.log(res))

        //Скрыть модальное окно
        .then(() => {
            $('#deleteModal').modal('hide')
        })

        //Обновить таблицу
        .then(() => {
            fetch(URL)
                .then(res => res.json())
                .then(data => {
                    renderTable(data)
                })
        })
})


// Create - insert new user
// Method: Post
formAddUser.addEventListener('submit', (e) => {
    e.preventDefault();

    // Формирование ролей
    const selectedRoles = [...formAddUser.elements.roleNames.options].filter(option => option.selected).map(option => option.value)
    selectedRoles.forEach(role => console.log(role))
    const roles = [];
    for (let i = 0; i < selectedRoles.length; i++) {
        roles.push({
            role: selectedRoles[i]
        });
    }

    fetch(URL, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            name: formAddUser.elements.name.value,
            age: formAddUser.elements.age.value,
            email: formAddUser.elements.email.value,
            password: formAddUser.elements.password.value,
            passwordConfirm: formAddUser.elements.passwordConfirm.value,
            roles: roles
        })
    })

        // получаем ответ на запрос
        .then(res => res.json())

        //Сбросить значения полей
        .then(() => {
            formAddUser.elements.name.value = ''
            formAddUser.elements.age.value = ''
            formAddUser.elements.email.value = ''
            formAddUser.elements.password.value = ''
            formAddUser.elements.passwordConfirm.value = ''
        })

        //Обновить таблицу
        .then(() => {
            fetch(URL)
                .then(res => res.json())
                .then(data => {
                    renderTable(data)
                })
        })

    const usersTableTab = document.body.querySelector('#usersTable-tab')
    usersTableTab.click()
})







