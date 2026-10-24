<?php

namespace App\Http\Controllers;

use App\Services\DashboardService;
use Illuminate\Http\Request;

class DashboardController extends Controller
{
    public function __construct(private readonly DashboardService $service)
    {
    }
    public function index()
    {
        return view('admin.dashboard', [
            'metricas' => $this->service->getDashboardMetricas()
        ]);
    }
}
