<template>
  <div>
    <div class="container">
      <div class="page-header">
        <h3>Добавление источника новостей</h3>
      </div>
      <alert-error :message="messageErrorNewsTemplate" :showAlert="showErrorNewsTemplate" id="alert-error-news-template"></alert-error>
      <alert-error :message="messageErrorListNewsTemplate" :showAlert="showErrorListNewsTemplate" id="alert-error-list-news-template"></alert-error>
      <alert-error :message="messageErrorNews" :showAlert="showErrorNews" id="alert-error-news"></alert-error>
      <alert-error :message="messageClientError" :showAlert="showClientError" id="alert-client-error"></alert-error>
      <alert-error :message="messageUnknownError" :showAlert="showUnknownError" id="alert-unknown-error"></alert-error>
      <!--alert-error-list :errors="errors" :showAlert="showFormError"></alert-error-list-->

      <div class="row my-4">
        <div class="col lg-6">
          <label for="news-template-list">Список источников новостей</label>
          <ul class="list-group" id="news-template-list">
            <li class="list-group-item py-2" v-for="newsTemplate in filteredNewsTemplate" v-bind:value="newsTemplate" v-bind:key="newsTemplate.id">{{ newsTemplate.sourceUrl }}</li>
          </ul>
        </div>
        <div class="col lg-6">
          <form @submit.prevent="submitNewsTemplate">
            <div class="form-group">
              <label for="input-url">URL ленты новостей</label>
              <input type="text" class="form-control" id="input-url" v-model="sourceUrl" placeholder />
            </div>
            <div class="form-group">
              <label for="input-itemTemplate">Шаблон элемента с новостью</label>
              <input type="text" class="form-control" id="input-itemTemplate" v-model="itemTemplate" placeholder />
            </div>
            <div class="form-group">
              <label for="input-linkTemplate">Шаблон адреса новости</label>
              <input type="text" class="form-control" id="input-linkTemplate" v-model="linkTemplate" placeholder />
            </div>
            <div class="form-group">
              <label for="input-timeTemplate">Шаблон времени новости</label>
              <input type="text" class="form-control" id="input-timeTemplate" v-model="timeTemplate" placeholder />
            </div>
            <div class="form-group">
              <label for="input-titleTemplate">Шаблон заголовка новости</label>
              <input type="text" class="form-control" id="input-titleTemplate" v-model="titleTemplate" placeholder />
            </div>
            <div class="form-group">
              <label for="input-type">Тип ленты новостей</label>
              <div class="form-check">
                <input class="form-check-input" type="radio" name="sourceType" id="sourceTypeWeb" value="website" v-model="type" checked />
                <label class="form-check-label" for="exampleRadios1">Website</label>
              </div>
              <div class="form-check">
                <input class="form-check-input" type="radio" name="sourceType" id="sourceTypeRss" value="rss" v-model="type" />
                <label class="form-check-label" for="exampleRadios2">Rss</label>
              </div>              
            </div>
            <div class="form-group">
              <label for="input-timeZone">Временная зона источника новостей</label>
              <input type="text" class="form-control" id="input-timeZone" v-model="timeZone" placeholder="UTC+03:00" />
            </div>
            <!--div class="form-check">
              <input class="form-check-input" type="checkbox" id="input-absLink" v-model="absLink" />
              <label for="input-absLink">Новости ленты с абсолютной ссылкой</label>              
            </div-->
            <button type="submit" class="btn btn-primary">Добавить источник новостей</button>
          </form>
        </div>
      </div>

      <div class="page-header">
        <h3>Список новостей</h3>
      </div>
      <div class="row my-4">
        <div class="col lg-6">
          <div class="form-group">
            <label for="input-titleSubstring">Подстрока в заголовке</label>
            <input type="text" class="form-control" id="input-titleSubstring" v-model="titleSubstring" />
          </div>
        </div>
        <div class="col lg-6">
          <div class="form-group">
            <label for="select-newsTemplate">Источник новостей</label>
            <select class="form-control" v-model="selectedNewsTemplate" id="select-newsTemplate">
              <option v-for="newsTemplate in newsTemplates" v-bind:value="newsTemplate" v-bind:key="newsTemplate.id">{{ newsTemplate.sourceUrl }}</option>
            </select>
          </div>
          <div class="row justify-content-center my-4">
            <button class="btn btn-primary" v-on:click="getNews">Показать новости</button>
          </div>
        </div>
      </div>

      <vue-good-table :columns="columns" :rows="rows" styleClass="vgt-table condensed bordered" :pagination-options="{
          enabled: true,
          mode: 'pages',
          perPageDropdown: [10, 15, 20]
        }">
        <template slot="table-row" slot-scope="props">
          <span v-if="props.column.field == 'link'">
            <a v-bind:href="props.row.link">{{props.row.link}}</a>
          </span>
          <span v-else>{{props.formattedRow[props.column.field]}}</span>
        </template>
      </vue-good-table>
    </div>
  </div>
</template>

<script>
import AlertError from "@/components/AlertError";
import { AXIOS } from "./http-common";
import "vue-good-table/dist/vue-good-table.css";
import { VueGoodTable } from "vue-good-table";

export default {
  name: "Aggregator",
  components: {
    "alert-error": AlertError,
    VueGoodTable
  },
  data() {
    return {
      newsTemplates: [],
      sourceUrl: "",
      itemTemplate: "",
      linkTemplate: "",
      timeTemplate: "",
      titleTemplate: "",
      type: "website",
      timeZone: "",
      absLink: false,
      titleSubstring: "",
      selectedNewsTemplate: null,
      columns: [
        {
          label: "Дата",
          field: "pubDate"
        },
        {
          label: "Заголовок",
          field: "title"
        },
        {
          label: "Адрес",
          field: "link"
        }
      ],
      rows: [],
      errors: [],
      showFormError: false,
      showErrorNewsTemplate: false,
      messageErrorNewsTemplate:
        "Ошибка добавления шаблона новостей на стороне сервера",
      showErrorListNewsTemplate: false,
      messageErrorListNewsTemplate:
        "Ошибка получения шаблонов новостей на стороне сервера",
      showErrorNews: false,
      messageErrorNews: "Ошибка получения новостей на стороне сервера",  
      messageClientError: "",
      showClientError: false,
      messageUnknownError: "",
      showUnknownError: false
    };
  },
  mounted() {
    this.newsTemplateList();
  },
  computed: {
    //Чтобы в списке добавленных источников отсутсовол пустой источник
    filteredNewsTemplate() {
      return this.newsTemplates.filter(newsTemplate => {
        return newsTemplate.id != null;
      });
    }
  },
  methods: {
    async newsTemplateList() {
      await AXIOS.get("/news-templates")
        .then(
          response => (
            (this.newsTemplates = response.data),
            this.newsTemplates.unshift({
              id: null,
              sourceUrl: ""
            }),
            (this.selectedNewsTemplate =
              this.newsTemplates.length != 0
                ? this.newsTemplates[0]
                : { id: null, sourceUrl: "" }),            
            (this.showClientError = false),
            (this.showUnknownError = false)
          )
        )
        .catch(err => {
          if (err.response) {
            if (err.response.data.error === "SERVER_ERROR") {
              this.messageErrorNewsTemplate =
                "Ошибка получения списка шаблонов новостей на стороне сервера.";
            }
            this.showErrorListNewsTemplate = true;
          } else if (err.request) {
            this.messageClientError = "Ошибка сети";
            this.showClientError = true;
          } else {
            this.messageUnknownError = "Неизвестная ошибка";
            this.showUnknownError = true;
          }
        });
    },
    async submitNewsTemplate() {
      /*
      this.errors = [];
      if (!this.sourceUrl) {
        this.errors.push("Укажите URL ленты новостей");
      }
      */
      //if (!this.errors.length) {
        await AXIOS.post("/news-template", {
          sourceUrl: this.sourceUrl.trim(),
          itemTemplate: this.itemTemplate.trim(),
          linkTemplate: this.linkTemplate.trim(),
          timeTemplate: this.timeTemplate.trim(),
          titleTemplate: this.titleTemplate.trim(),
          type: this.type,
          timeZone: this.timeZone.trim(),
        })
          .then(
            (this.showErrorNewsTemplate = false),
            (this.showClientError = false),
            (this.showUnknownError = false)
          )
          .catch(err => {
            if (err.response) {
              if (err.response.data.error === "USER_ERROR") {
                this.messageErrorNewsTemplate =
                  "Ошибка добавления шаблона новостей со стороны клиента";
              } else if (err.response.data.error === "SERVER_ERROR") {
                this.messageErrorNewsTemplate = err.response.data.error_message;
                  //"Ошибка добавления шаблона новостей на стороне сервера";
              }
              this.showErrorNewsTemplate = true;
            } else if (err.request) {
              this.messageClientError = "Ошибка сети";
              this.showClientError = true;
            } else {
              this.messageUnknownError = "Неизвестная ошибка";
              this.showUnknownError = true;
            }
          });
        this.sourceUrl = "";
        (this.itemTemplate = ""),
          (this.linkTemplate = ""),
          (this.timeTemplate = ""),
          (this.titleTemplate = ""),
          (this.type = "website"),
          (this.timeZone = ""),
          this.newsTemplateList();
      /*
      } else {
        this.showFormError = true;
      }
      */
    },
    getNews() {
      this.rows = [];
      this.errors = [];

      AXIOS.get("/news", {
        params: {
          idStr: this.selectedNewsTemplate.id,
          title: this.titleSubstring
        }
      })
        .then(
          response => (
            (this.rows = this.fillRows(response.data)),
            (this.showErrorNews = false),
            (this.showClientError = false),
            (this.showUnknownError = false)
          )
        )
        .catch(err => {
          if (err.response) {
            if (err.response.data.error === "USER_ERROR") {
              this.messageErrorNews =
                "Ошибка запроса новостей со стороны клиента. В запросе некорректно указан идентификатор шаблона новости";
            } else if (err.response.data.error === "SERVER_ERROR") {
              this.messageErrorNews =
                "Ошибка запроса новостей. Внутренняя ошибка сервера.";
            }
            this.showErrorNews = true;
          } else if (err.request) {
            this.messageClientError = "Ошибка сети";
            this.showClientError = true;
          } else {
            this.messageUnknownError = "Неизвестная ошибка";
            this.showUnknownError = true;
          }
        });
    },
    fillRows: function(data) {
      let rows = [];

      data.forEach(function(item) {
        let row = {
          id: item.id,
          pubDate: item.pubDate,
          title: item.title,
          link: item.link
        };
        rows.push(row);
      });
      return rows;
    }
  }
};
</script>


<style scoped>
</style>