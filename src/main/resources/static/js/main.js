const SERVER_URL = 'http://localhost:8080/api/v1/';

document.getElementById('form-recipe').addEventListener('submit', getRecipe);
document.getElementById('form-recipe2').addEventListener('submit', getRecipeWithRateLimit);
document.getElementById('form-info').addEventListener('submit', getProjectInfo);

async function getRecipe(event) {
  event.preventDefault();

  const URL = `${SERVER_URL}recipe/generate?ingredients=${document.getElementById('ingredients').value}`;
  const spinner = document.getElementById('spinner1');
  const result = document.getElementById('result');
  result.style.color = "black";

  try {
    spinner.style.display = "block";
    const response = await fetch(URL).then(handleHttpErrors);
    document.getElementById('result').innerText = response.answer;
  } catch (e) {
    result.style.color = "red";
    result.innerText = e.message;
  } finally {
    spinner.style.display = "none";
  }
}

async function getRecipeWithRateLimit(event) {
  event.preventDefault();

  const URL = `${SERVER_URL}recipelimited/generate?ingredients=${document.getElementById('ingredients2').value}`;
  const result2 = document.getElementById('result2');
  const spinner2 = document.getElementById('spinner2');
  result2.style.color = "black";
  result2.innerText = "";

  try {
    spinner2.style.display = "block";
    const response = await fetch(URL).then(handleHttpErrors);
    document.getElementById('result2').innerText = response.answer;
  } catch (e) {
    result2.style.color = "red";
    result2.innerText = e.message;
  } finally {
    spinner2.style.display = "none";
  }
}

async function getProjectInfo(event) {
  event.preventDefault();

  const URL = `${SERVER_URL}projectinfo?question=${document.getElementById('the-question').value}`;
  const spinner = document.getElementById('spinner3');
  const result3 = document.getElementById('result3');
  result3.innerText = "";
  result3.style.color = "black";

  try {
    spinner.style.display = "block";
    const reply = await fetch(URL).then(handleHttpErrors);
    document.getElementById('result3').innerHTML = convertToLink(reply.answer);
  } catch (e) {
    result3.style.color = "red";
    result3.innerText = e.message;
  } finally {
    spinner.style.display = "none";
  }
}

async function handleHttpErrors(res) {
  if (!res.ok) {
    const errorResponse = await res.json();
    const msg = errorResponse.message ? errorResponse.message : "No error details provided";
    throw new Error(msg);
  }
  return res.json();
}

function convertToLink(str) {
  const urlRegex = /(https?:\/\/[^\s]+)/g;
  return str.replace(urlRegex, function(match) {
    if (match.endsWith('.')) {
      match = match.slice(0, -1);
    }
    return `<a href="${match}" target="_blank">${match}</a>`;
  });
}
