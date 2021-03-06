"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
const typeorm_1 = require("typeorm");
const SavedWord_1 = require("./SavedWord");
let Pronunciation = class Pronunciation {
};
__decorate([
    typeorm_1.PrimaryGeneratedColumn("uuid"),
    __metadata("design:type", String)
], Pronunciation.prototype, "id", void 0);
__decorate([
    typeorm_1.Column(),
    __metadata("design:type", String)
], Pronunciation.prototype, "symbol", void 0);
__decorate([
    typeorm_1.PrimaryColumn(),
    __metadata("design:type", String)
], Pronunciation.prototype, "audio", void 0);
__decorate([
    typeorm_1.PrimaryColumn("uuid"),
    __metadata("design:type", String)
], Pronunciation.prototype, "savedWordId", void 0);
__decorate([
    typeorm_1.ManyToOne(() => SavedWord_1.SavedWord, (word) => word.pronunciations, {
        onDelete: "CASCADE",
        onUpdate: "CASCADE",
    }),
    typeorm_1.JoinColumn({ name: "savedWordId" }),
    __metadata("design:type", SavedWord_1.SavedWord)
], Pronunciation.prototype, "word", void 0);
__decorate([
    typeorm_1.CreateDateColumn(),
    __metadata("design:type", Date)
], Pronunciation.prototype, "createdAt", void 0);
__decorate([
    typeorm_1.UpdateDateColumn(),
    __metadata("design:type", Date)
], Pronunciation.prototype, "updatedAt", void 0);
Pronunciation = __decorate([
    typeorm_1.Entity()
], Pronunciation);
exports.Pronunciation = Pronunciation;
//# sourceMappingURL=Pronunciation.js.map