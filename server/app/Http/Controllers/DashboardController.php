<?php

namespace App\Http\Controllers;

use App\Services\DashboardService;
use Illuminate\Http\Request;

class DashboardController extends Controller
{
    private DashboardService $service;
    public function __construct(DashboardService $service)
    {
        $this->service = $service;
    }
    public function index()
    {
        return view('admin.dashboard', [
            'metricas' => $this->service->getDashboardMetricas()
        ]);
    }
}
