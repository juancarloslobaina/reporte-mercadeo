import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { ReporteService } from '../entities/reporte/service/reporte.service';
import { Account } from 'app/core/auth/account.model';
import { CalendarOptions, DateSelectArg, EventApi, EventClickArg, EventInput } from '@fullcalendar/core'; // useful for typechecking
import interactionPlugin from '@fullcalendar/interaction';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import listPlugin from '@fullcalendar/list';
import esLocale from '@fullcalendar/core/locales/es';
import dayjs from 'dayjs/esm';
import { DATE_FORMAT, DATE_TIME_FORMAT } from '../config/input.constants';
import { IReporte } from '../entities/reporte/reporte.model';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  currentEvents: EventApi[] = [];

  private readonly destroy$ = new Subject<void>();

  calendarVisible = true;
  calendarOptions: CalendarOptions = {
    plugins: [interactionPlugin, dayGridPlugin, timeGridPlugin, listPlugin],
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek',
    },
    locale: esLocale,
    initialView: 'dayGridMonth',
    //initialEvents: INITIAL_EVENTS, // alternatively, use the `events` setting to fetch from a feed
    weekends: true,
    editable: true,
    selectable: true,
    selectMirror: true,
    dayMaxEvents: true,
    select: this.handleDateSelect.bind(this),
    eventClick: this.handleEventClick.bind(this),
    eventsSet: this.handleEvents.bind(this),
    events: [],
    //events: this.getReporteEvents(),
    /* you can update a remote database when these fire:
    eventAdd:
    eventChange:
    eventRemove:
    */
  };

  constructor(
    private accountService: AccountService,
    private router: Router,
    private changeDetector: ChangeDetectorRef,
    private reporteService: ReporteService
  ) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));

    let date = new Date(),
      year = date.getFullYear(),
      month = date.getMonth();
    let firstDay = new Date(year, month, 1);
    let lastDay = new Date(year, month + 1, 0);
    const queryObject: any = {
      page: 0,
      size: 20,
      'fecha.greaterThanOrEqual': dayjs(firstDay.toString()).toISOString(),
      'fecha.lessThanOrEqual': dayjs(lastDay.toString()).toISOString(),
      sort: ['id,asc'],
    };
    this.reporteService
      .search(queryObject)
      .pipe(map((res: HttpResponse<IReporte[]>) => res.body ?? []))
      .pipe(
        map(events =>
          events.map(ev => ({
            id: ev.centro?.id.toString(),
            title: ev.centro?.nombre?.toString(),
            start: ev.fecha?.format(DATE_FORMAT),
          }))
        )
      )
      .subscribe(
        (data: EventInput[]) =>
          (this.calendarOptions.events = data.filter((value, index) => data.findIndex(o => value.id === o.id) === index))
      );
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  handleCalendarToggle() {
    this.calendarVisible = !this.calendarVisible;
  }

  handleWeekendsToggle() {
    const { calendarOptions } = this;
    calendarOptions.weekends = !calendarOptions.weekends;
  }

  handleDateSelect(selectInfo: DateSelectArg) {
    const queryParamsObj = {
      fecha: dayjs(selectInfo.start).format(DATE_TIME_FORMAT),
    };
    this.router.navigate(['/reporte/new'], {
      queryParams: queryParamsObj,
    });
  }

  handleEventClick(clickInfo: EventClickArg) {
    const queryParamsObj = {
      'filter[centroId.equals]': clickInfo.event.id,
    };
    this.router.navigate(['/reporte'], {
      queryParams: queryParamsObj,
    });
  }

  handleEvents(events: EventApi[]) {
    this.currentEvents = events;
    this.changeDetector.detectChanges();
  }
}
